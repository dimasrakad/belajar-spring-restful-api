package programmerzamannow.restful.services;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import programmerzamannow.restful.entities.Address;
import programmerzamannow.restful.entities.Contact;
import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.models.addresses.AddressResponse;
import programmerzamannow.restful.models.addresses.CreateAddressRequest;
import programmerzamannow.restful.models.addresses.UpdateAddressRequest;
import programmerzamannow.restful.repositories.AddressRepository;
import programmerzamannow.restful.repositories.ContactRepository;
import programmerzamannow.restful.util.ObjectUtil;

@Service
public class AddressService {
        @Autowired
        AddressRepository addressRepository;

        @Autowired
        ContactRepository contactRepository;

        @Autowired
        ValidationService validationService;

        @Transactional
        public AddressResponse create(User user, CreateAddressRequest request, String idContact) {
                validationService.validate(request);

                Contact contact = contactRepository.findFirstByUserAndId(user, idContact)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Contact is not found"));

                Address address = new Address();
                address.setId(UUID.randomUUID().toString());
                address.setStreet(request.getStreet());
                address.setCity(request.getCity());
                address.setProvince(request.getProvince());
                address.setCountry(request.getCountry());
                address.setPostalCode(request.getPostalCode());
                address.setContact(contact);

                addressRepository.save(address);

                return toAddressResponse(address);
        }

        public AddressResponse get(User user, String idContact, String idAddress) {
                Contact contact = contactRepository.findFirstByUserAndId(user, idContact)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Contact is not found"));

                Address address = addressRepository.findFirstByContactAndId(contact, idAddress)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Address is not found"));

                return toAddressResponse(address);
        }

        @Transactional
        public AddressResponse update(User user, String idContact, String idAddress, UpdateAddressRequest request) {
                validationService.validate(request);

                Contact contact = contactRepository.findFirstByUserAndId(user, idContact)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Contact is not found"));
                Address address = addressRepository.findFirstByContactAndId(contact, idAddress)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Address is not found"));

                ObjectUtil.setIfNotNull(request.getStreet(), address::setStreet);
                ObjectUtil.setIfNotNull(request.getCity(), address::setCity);
                ObjectUtil.setIfNotNull(request.getProvince(), address::setProvince);
                ObjectUtil.setIfNotNull(request.getCountry(), address::setCountry);
                ObjectUtil.setIfNotNull(request.getPostalCode(), address::setPostalCode);

                addressRepository.save(address);

                return toAddressResponse(address);
        }

        public List<AddressResponse> list(User user, String idContact, String sortBy, String sortDirection) {
                Contact contact = contactRepository.findFirstByUserAndId(user, idContact)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Contact is not found"));

                try {
                        validationService.validateSort(sortBy, sortDirection, Address.class);
                } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                }

                Sort sort = Sort.unsorted();

                if (Objects.nonNull(sortBy) && !sortBy.isBlank()) {
                        Sort.Direction direction = Sort.Direction.fromString(
                                        (Objects.nonNull(sortDirection) && !sortDirection.isBlank())
                                                        ? sortDirection
                                                        : "asc");

                        sort = Sort.by(direction, sortBy);
                }

                List<Address> addresses = addressRepository.findByContact(contact, sort);

                return addresses.stream()
                                .map(this::toAddressResponse)
                                .toList();
        }

        @Transactional
        public void delete(User user, String idContact, String idAddress) {
                Contact contact = contactRepository.findFirstByUserAndId(user, idContact)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Contact is not found"));

                Address address = addressRepository.findFirstByContactAndId(contact, idAddress)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Address is not found"));

                addressRepository.delete(address);
        }

        private AddressResponse toAddressResponse(Address address) {
                return AddressResponse.builder()
                                .id(address.getId())
                                .street(address.getStreet())
                                .city(address.getCity())
                                .province(address.getProvince())
                                .country(address.getCountry())
                                .postalCode(address.getPostalCode())
                                .build();
        }
}
