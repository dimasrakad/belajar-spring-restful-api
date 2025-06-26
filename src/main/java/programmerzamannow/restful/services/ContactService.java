package programmerzamannow.restful.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import jakarta.persistence.criteria.Predicate;
import programmerzamannow.restful.entities.Contact;
import programmerzamannow.restful.entities.ContactCategory;
import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.models.contacts.ContactResponse;
import programmerzamannow.restful.models.contacts.CreateContactRequest;
import programmerzamannow.restful.models.contacts.SearchContactRequest;
import programmerzamannow.restful.models.contacts.SelectContactsRequest;
import programmerzamannow.restful.models.contacts.UpdateContactRequest;
import programmerzamannow.restful.properties.PhotoStorageProperties;
import programmerzamannow.restful.repositories.ContactCategoryRepository;
import programmerzamannow.restful.repositories.ContactRepository;
import programmerzamannow.restful.util.ObjectUtil;
import programmerzamannow.restful.util.SpecificationUtil;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactCategoryRepository contactCategoryRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private PhotoStorageProperties photoStorageProperties;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Transactional
    public ContactResponse create(User user, CreateContactRequest request) {
        validationService.validate(request);

        ContactCategory contactCategory = null;

        if (request.getCategoryId() != null) {
            contactCategory = contactCategoryRepository
                    .findFirstByUserAndId(user, request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        }

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        ObjectUtil.setIfNotNull(contactCategory, contact::setContactCategory);
        contact.setUser(user);

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    public ContactResponse storePhoto(User user, MultipartFile file, String id) throws IOException {
        Contact contact = contactRepository.findFirstByUserAndId(user, id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found");
        });

        String uploadDir = photoStorageProperties.getUploadDir();

        Path uploadPath = Paths.get(uploadDir, contact.getUser().getUsername());

        deletePhotoIfExist(contact);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        String baseName = contact.getId();

        if (originalFilename != null && originalFilename.contains(".")) {
            int dotIndex = originalFilename.lastIndexOf(".");
            fileExtension = originalFilename.substring(dotIndex);
            baseName = contact.getId() + fileExtension;
        }

        String savedFileName = baseName;

        Path filePath = uploadPath.resolve(savedFileName);
        file.transferTo(filePath);

        contact.setPhoto(savedFileName);
        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    public ContactResponse get(User user, String id) {
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        return toContactResponse(contact);
    }

    public Resource getPhoto(User user, String photo) {
        String id = photo.substring(0, photo.indexOf("."));

        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Path photoPath = Paths.get(photoStorageProperties.getUploadDir(), contact.getUser().getUsername(), photo);

        Resource resource = new FileSystemResource(photoPath);

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo file not found");
        }

        return resource;
    }

    public void deletePhoto(User user, String photo) {
        if (photo == null || photo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Photo must be filled");
        }

        String id = photo.substring(0, photo.indexOf("."));

        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        if (contact.getPhoto() == null || contact.getPhoto().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo file not found");
        }

        try {
            String uploadDir = photoStorageProperties.getUploadDir();
            Path photoToDelete = Paths.get(uploadDir, contact.getUser().getUsername(), photo);

            if (Files.exists(photoToDelete)) {
                Files.delete(photoToDelete);

                contact.setPhoto(null);
                contactRepository.save(contact);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete photo: " + e.getMessage());
        }
    }

    @Transactional
    public ContactResponse update(User user, String id, UpdateContactRequest request) {
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        ContactCategory contactCategory = null;

        if (request.getCategoryId() != null) {
            contactCategory = contactCategoryRepository
                    .findFirstByUserAndId(user, request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        }

        ObjectUtil.setIfNotNull(request.getFirstName(), contact::setFirstName);
        ObjectUtil.setIfNotNull(request.getLastName(), contact::setLastName);
        ObjectUtil.setIfNotNull(request.getEmail(), contact::setEmail);
        ObjectUtil.setIfNotNull(request.getPhone(), contact::setPhone);
        ObjectUtil.setIfNotNull(contactCategory, contact::setContactCategory);

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    public Page<ContactResponse> search(User user, SearchContactRequest request) {
        try {
            validationService.validateSort(request.getSortBy(), request.getSortDirection(), Contact.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        Specification<Contact> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user"), user));

            if (Objects.nonNull(request.getName()) && !request.getName().isBlank()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("firstName"), "%" + request.getName() + "%"),
                        criteriaBuilder.like(root.get("lastName"), "%" + request.getName() + "%")));
            }

            SpecificationUtil.addLikePredicateIfPresent(request.getEmail(), "email", root, criteriaBuilder, predicates);
            SpecificationUtil.addLikePredicateIfPresent(request.getPhone(), "phone", root, criteriaBuilder, predicates);

            return query.where(predicates.toArray(new Predicate[] {})).getRestriction();
        };

        Sort sort = Sort.unsorted();

        if (Objects.nonNull(request.getSortBy()) && !request.getSortBy().isBlank()) {
            Sort.Direction direction = Sort.Direction.fromString(
                    (Objects.nonNull(request.getSortDirection()) && !request.getSortDirection().isBlank())
                            ? request.getSortDirection()
                            : "asc");

            sort = Sort.by(direction, request.getSortBy());
        }

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort);

        Page<Contact> contacts = contactRepository.findAll(specification, pageable);

        List<ContactResponse> responses = contacts.getContent().stream()
                .map(this::toContactResponse)
                .toList();

        return new PageImpl<>(responses, pageable, contacts.getTotalElements());
    }

    @Transactional
    public void delete(User user, String id) {
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        deletePhotoIfExist(contact);

        contactRepository.delete(contact);
    }

    @Transactional
    public void bulkDelete(User user, SelectContactsRequest request) {
        validationService.validate(request);

        List<Contact> contacts = contactRepository.findAllByUserAndIdIn(user, request.getIds());

        contacts.forEach(contact -> {
            deletePhotoIfExist(contact);

            contactRepository.delete(contact);
        });
    }

    private ContactResponse toContactResponse(Contact contact) {
        String url = serverAddress + ((serverPort != null && !serverPort.isEmpty()) ? ":" + serverPort : "")
                + contextPath + "/contact/photo/";

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .photoPath(
                        (contact.getPhoto() != null && !contact.getPhoto().isEmpty()) ? url + contact.getPhoto()
                                : null)
                .categoryId(contact.getContactCategory().getId())
                .categoryName(contact.getContactCategory().getName())
                .build();
    }

    private void deletePhotoIfExist(Contact contact) {
        if (contact.getPhoto() != null && !contact.getPhoto().isEmpty()) {
            try {
                String uploadDir = photoStorageProperties.getUploadDir();
                Path photoToDelete = Paths.get(uploadDir, contact.getUser().getUsername(), contact.getPhoto());

                if (Files.exists(photoToDelete)) {
                    Files.delete(photoToDelete);
                }
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to delete photo: " + e.getMessage());
            }
        }
    }
}
