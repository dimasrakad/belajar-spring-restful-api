package programmerzamannow.restful.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.CreateAddressRequest;
import programmerzamannow.restful.model.SortResponse;
import programmerzamannow.restful.model.UpdateAddressRequest;
import programmerzamannow.restful.model.AddressResponse;
import programmerzamannow.restful.model.WebResponse;
import programmerzamannow.restful.service.AddressService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class AddressController {
    @Autowired
    private AddressService addressService;

    private final String path = "/api/contact/{idContact}/address";

    @PostMapping(path)
    public WebResponse<AddressResponse> create(User user, @PathVariable String idContact,
            @RequestBody CreateAddressRequest request) {
        AddressResponse response = addressService.create(user, request, idContact);

        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @GetMapping(path + "/{idAddress}")
    public WebResponse<AddressResponse> get(User user, @PathVariable String idContact, @PathVariable String idAddress) {
        AddressResponse response = addressService.get(user, idContact, idAddress);

        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @PatchMapping(path + "/{idAddress}")
    public WebResponse<AddressResponse> update(User user, @PathVariable String idContact,
            @PathVariable String idAddress, @RequestBody UpdateAddressRequest request) {
        AddressResponse response = addressService.update(user, idContact, idAddress, request);

        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @GetMapping(path)
    public WebResponse<List<AddressResponse>> list(User user,
            @PathVariable String idContact,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        List<AddressResponse> responses = addressService.list(user, idContact, sortBy, sortDirection);
        SortResponse sortResponse = SortResponse.builder()
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        return WebResponse.<List<AddressResponse>>builder()
                .data(responses)
                .sort(sortResponse)
                .build();
    }

    @DeleteMapping(path + "/{idAddress}")
    public WebResponse<String> delete(User user, @PathVariable String idContact, @PathVariable String idAddress) {
        addressService.delete(user, idContact, idAddress);

        return WebResponse.<String>builder()
                .data(null)
                .build();
    }
}
