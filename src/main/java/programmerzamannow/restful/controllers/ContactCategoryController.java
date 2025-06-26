package programmerzamannow.restful.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.enums.CategoryColor;
import programmerzamannow.restful.models.WebResponse;
import programmerzamannow.restful.models.contactcategories.ContactCategoryResponse;
import programmerzamannow.restful.models.contactcategories.CreateContactCategoryRequest;
import programmerzamannow.restful.models.contactcategories.UpdateContactCategoryRequest;
import programmerzamannow.restful.services.ContactCategoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(path = "/contact-category")
public class ContactCategoryController {
    @Autowired
    ContactCategoryService contactCategoryService;

    @PostMapping
    public WebResponse<ContactCategoryResponse> create(User user, @RequestBody CreateContactCategoryRequest request) {
        ContactCategoryResponse response = contactCategoryService.create(user, request);

        return WebResponse.<ContactCategoryResponse>builder().data(response).build();
    }

    @GetMapping("/{id}")
    public WebResponse<ContactCategoryResponse> get(User user, @PathVariable Long id) {
        ContactCategoryResponse response = contactCategoryService.get(user, id);

        return WebResponse.<ContactCategoryResponse>builder().data(response).build();
    }

    @GetMapping("/colors")
    public WebResponse<List<CategoryColor>> getColors() {
        List<CategoryColor> response = contactCategoryService.getColors();

        return WebResponse.<List<CategoryColor>>builder().data(response).build();
    }

    @GetMapping
    public WebResponse<List<ContactCategoryResponse>> list(User user) {
        List<ContactCategoryResponse> response = contactCategoryService.list(user);

        return WebResponse.<List<ContactCategoryResponse>>builder().data(response).build();
    }

    @PatchMapping("/{id}")
    public WebResponse<ContactCategoryResponse> update(User user, @PathVariable Long id, @RequestBody UpdateContactCategoryRequest request) {
        ContactCategoryResponse response = contactCategoryService.update(user, id, request);

        return WebResponse.<ContactCategoryResponse>builder().data(response).build();
    }

    @DeleteMapping("/{id}")
    public WebResponse<String> delete(User user, @PathVariable Long id) {
        contactCategoryService.delete(user, id);

        return WebResponse.<String>builder().data(null).build();
    }
}
