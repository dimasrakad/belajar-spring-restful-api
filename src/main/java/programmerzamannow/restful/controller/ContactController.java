package programmerzamannow.restful.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.UpdateContactRequest;
import programmerzamannow.restful.model.ContactResponse;
import programmerzamannow.restful.model.CreateContactRequest;
import programmerzamannow.restful.model.PaginationResponse;
import programmerzamannow.restful.model.SearchContactRequest;
import programmerzamannow.restful.model.SortResponse;
import programmerzamannow.restful.model.WebResponse;
import programmerzamannow.restful.service.ContactService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ContactController {
    final String path = "/api/contact";

    @Autowired
    private ContactService contactService;

    @PostMapping(path)
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request) {
        ContactResponse response = contactService.create(user, request);

        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @GetMapping(path + "/{contactId}")
    public WebResponse<ContactResponse> get(User user, @PathVariable String contactId) {
        ContactResponse response = contactService.get(user, contactId);

        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @PatchMapping(path + "/{contactId}")
    public WebResponse<ContactResponse> update(User user, @PathVariable String contactId,
            @RequestBody UpdateContactRequest request) {
        ContactResponse response = contactService.update(user, contactId, request);

        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @GetMapping(path)
    public WebResponse<List<ContactResponse>> search(User user,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        SearchContactRequest request = new SearchContactRequest(name, phone, email, page, size, sortBy, sortDirection);
        Page<ContactResponse> response = contactService.search(user, request);
        PaginationResponse paginationResponse = PaginationResponse.builder()
                .currentPage(response.getNumber())
                .totalPage(response.getTotalPages())
                .size(response.getSize())
                .build();
        SortResponse sortResponse = SortResponse.builder()
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        return WebResponse.<List<ContactResponse>>builder()
                .data(response.getContent())
                .pagination(paginationResponse)
                .sort(sortResponse)
                .build();
    }

    @DeleteMapping(path + "/{contactId}")
    public WebResponse<String> delete(User user, @PathVariable String contactId) {
        contactService.delete(user, contactId);

        return WebResponse.<String>builder().data(null).build();
    }
}
