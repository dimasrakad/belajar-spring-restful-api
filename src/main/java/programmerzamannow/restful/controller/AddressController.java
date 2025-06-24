package programmerzamannow.restful.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.SortResponse;
import programmerzamannow.restful.model.WebResponse;
import programmerzamannow.restful.model.address.AddressResponse;
import programmerzamannow.restful.model.address.CreateAddressRequest;
import programmerzamannow.restful.model.address.UpdateAddressRequest;
import programmerzamannow.restful.service.AddressService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(path = "/contact/{idContact}/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping()
    public WebResponse<AddressResponse> create(User user, @PathVariable String idContact,
            @RequestBody CreateAddressRequest request) {
        AddressResponse response = addressService.create(user, request, idContact);

        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @GetMapping("/{idAddress}")
    public WebResponse<AddressResponse> get(User user, @PathVariable String idContact, @PathVariable String idAddress) {
        AddressResponse response = addressService.get(user, idContact, idAddress);

        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @PatchMapping("/{idAddress}")
    public WebResponse<AddressResponse> update(User user, @PathVariable String idContact,
            @PathVariable String idAddress, @RequestBody UpdateAddressRequest request) {
        AddressResponse response = addressService.update(user, idContact, idAddress, request);

        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @GetMapping()
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

    @DeleteMapping("/{idAddress}")
    public WebResponse<String> delete(User user, @PathVariable String idContact, @PathVariable String idAddress) {
        addressService.delete(user, idContact, idAddress);

        return WebResponse.<String>builder()
                .data(null)
                .build();
    }
}
