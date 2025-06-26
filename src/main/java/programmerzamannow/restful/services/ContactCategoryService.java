package programmerzamannow.restful.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import programmerzamannow.restful.entities.Contact;
import programmerzamannow.restful.entities.ContactCategory;
import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.enums.CategoryColor;
import programmerzamannow.restful.models.contactcategories.ContactCategoryResponse;
import programmerzamannow.restful.models.contactcategories.CreateContactCategoryRequest;
import programmerzamannow.restful.models.contactcategories.UpdateContactCategoryRequest;
import programmerzamannow.restful.repositories.ContactCategoryRepository;
import programmerzamannow.restful.repositories.ContactRepository;
import programmerzamannow.restful.util.ObjectUtil;

@Service
public class ContactCategoryService {
    @Autowired
    private ContactCategoryRepository contactCategoryRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public ContactCategoryResponse create(User user, CreateContactCategoryRequest request) {
        validationService.validate(request);

        ContactCategory contactCategory = new ContactCategory();
        contactCategory.setName(request.getName());
        contactCategory.setColor(request.getColor());
        contactCategory.setUser(user);
        contactCategoryRepository.save(contactCategory);

        return toContactCategoryResponse(contactCategory);
    }

    public ContactCategoryResponse get(User user, Long id) {
        ContactCategory contactCategory = contactCategoryRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        return toContactCategoryResponse(contactCategory);
    }

    public List<CategoryColor> getColors() {
        return Arrays.asList(CategoryColor.values());
    }

    public List<ContactCategoryResponse> list(User user) {
        List<ContactCategory> contactCategories = contactCategoryRepository.findByUser(user);

        return contactCategories.stream()
                .map(this::toContactCategoryResponse)
                .toList();
    }

    @Transactional
    public ContactCategoryResponse update(User user, Long id, UpdateContactCategoryRequest request) {
        ContactCategory contactCategory = contactCategoryRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        ObjectUtil.setIfNotNull(contactCategory.getName(), contactCategory::setName);
        ObjectUtil.setIfNotNull(contactCategory.getColor(), contactCategory::setColor);

        contactCategoryRepository.save(contactCategory);

        return toContactCategoryResponse(contactCategory);
    }

    @Transactional
    public void delete(User user, Long id) {
        ContactCategory contactCategory = contactCategoryRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        List<Contact> contacts = contactRepository.findAllByUserAndContactCategory(user, contactCategory);

        contacts.forEach(contact -> contact.setContactCategory(null));
        
        contactCategoryRepository.delete(contactCategory);
    }

    private ContactCategoryResponse toContactCategoryResponse(ContactCategory contactCategory) {
        return ContactCategoryResponse.builder()
                .id(contactCategory.getId())
                .name(contactCategory.getName())
                .color(contactCategory.getColor())
                .build();
    }
}
