package programmerzamannow.restful.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import jakarta.persistence.criteria.Predicate;
import programmerzamannow.restful.entity.Contact;
import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.UpdateContactRequest;
import programmerzamannow.restful.model.ContactResponse;
import programmerzamannow.restful.model.CreateContactRequest;
import programmerzamannow.restful.model.SearchContactRequest;
import programmerzamannow.restful.repository.ContactRepository;
import programmerzamannow.restful.util.ObjectUtil;
import programmerzamannow.restful.util.SpecificationUtil;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public ContactResponse create(User user, CreateContactRequest request) {
        validationService.validate(request);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);

        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    public ContactResponse get(User user, String id) {
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        return toContactResponse(contact);
    }

    @Transactional
    public ContactResponse update(User user, String id, UpdateContactRequest request) {
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        ObjectUtil.setIfNotNull(request.getFirstName(), contact::setFirstName);
        ObjectUtil.setIfNotNull(request.getLastName(), contact::setLastName);
        ObjectUtil.setIfNotNull(request.getEmail(), contact::setEmail);
        ObjectUtil.setIfNotNull(request.getPhone(), contact::setPhone);

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

        contactRepository.delete(contact);
    }

    private ContactResponse toContactResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }
}
