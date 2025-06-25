package programmerzamannow.restful.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.models.PaginationResponse;
import programmerzamannow.restful.models.SortResponse;
import programmerzamannow.restful.models.WebResponse;
import programmerzamannow.restful.models.contacts.ContactResponse;
import programmerzamannow.restful.models.contacts.CreateContactRequest;
import programmerzamannow.restful.models.contacts.SearchContactRequest;
import programmerzamannow.restful.models.contacts.SelectContactsRequest;
import programmerzamannow.restful.models.contacts.UpdateContactRequest;
import programmerzamannow.restful.services.ContactService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequestMapping(path = "/contact")
public class ContactController {
    @Autowired
    private ContactService contactService;

    @PostMapping
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request) {
        ContactResponse response = contactService.create(user, request);

        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @GetMapping("/{contactId}")
    public WebResponse<ContactResponse> get(User user, @PathVariable String contactId) {
        ContactResponse response = contactService.get(user, contactId);

        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @PostMapping(path = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WebResponse<ContactResponse> storePhoto(User user, @RequestPart String id, @RequestPart MultipartFile file) {
        ContactResponse response;
        try {
            response = contactService.storePhoto(user, file, id);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload photo: " + e.getMessage());
        }
        
        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @GetMapping("/photo/{photo}")
    public ResponseEntity<Resource> getPhoto(User user, @PathVariable String photo) {
        Resource resource = contactService.getPhoto(user, photo);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
    }

    @DeleteMapping("/photo/{photo}")
    public WebResponse<String> deletePhoto(User user, @PathVariable String photo) {
        contactService.deletePhoto(user, photo);

        return WebResponse.<String>builder().data(null).build();
    }

    @PatchMapping("/{contactId}")
    public WebResponse<ContactResponse> update(User user, @PathVariable String contactId,
            @RequestBody UpdateContactRequest request) {
        ContactResponse response = contactService.update(user, contactId, request);

        return WebResponse.<ContactResponse>builder().data(response).build();
    }

    @GetMapping()
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

    @DeleteMapping("/{contactId}")
    public WebResponse<String> delete(User user, @PathVariable String contactId) {
        contactService.delete(user, contactId);

        return WebResponse.<String>builder().data(null).build();
    }

    @DeleteMapping()
    public WebResponse<String> bulkDelete(User user, @RequestBody SelectContactsRequest request) {
        contactService.bulkDelete(user, request);

        return WebResponse.<String>builder().data(null).build();
    }
}
