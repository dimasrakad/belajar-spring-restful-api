package programmerzamannow.restful.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import programmerzamannow.restful.entities.Address;
import programmerzamannow.restful.entities.Contact;
import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.models.WebResponse;
import programmerzamannow.restful.models.addresses.AddressResponse;
import programmerzamannow.restful.models.addresses.CreateAddressRequest;
import programmerzamannow.restful.repositories.AddressRepository;
import programmerzamannow.restful.repositories.ContactRepository;
import programmerzamannow.restful.repositories.UserRepository;
import programmerzamannow.restful.repositories.UserTokenRepository;
import programmerzamannow.restful.securities.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserTokenRepository userTokenRepository;

        @Autowired
        private AddressRepository addressRepository;

        @Autowired
        private ContactRepository contactRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                userTokenRepository.deleteAll();
                addressRepository.deleteAll();
                contactRepository.deleteAll();
                userRepository.deleteAll();

                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setEmail("test@test.com");
                user.setToken("test");
                user.setTokenExpiredAt(Instant.now().toEpochMilli() + 1000000000L);
                user.setIsVerified(true);
                userRepository.save(user);

                Contact contact = new Contact();
                contact.setId("test");
                contact.setFirstName("test");
                contact.setLastName("test");
                contact.setEmail("test@test.com");
                contact.setPhone("+628123456789");
                contact.setUser(user);
                contactRepository.save(contact);
        }

        @Test
        void testCreateSuccess() throws Exception {
                CreateAddressRequest request = new CreateAddressRequest();
                request.setStreet("test");
                request.setCity("test");
                request.setProvince("test");
                request.setCountry("test");
                request.setPostalCode("12345");

                mockMvc.perform(
                                post("/contact/test/address")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getData());

                                        Address address = addressRepository.findById(response.getData().getId())
                                                        .orElse(null);
                                        assertNotNull(address);
                                        assertEquals(response.getData().getStreet(), address.getStreet());
                                        assertEquals(response.getData().getCity(), address.getCity());
                                        assertEquals(response.getData().getProvince(), address.getProvince());
                                        assertEquals(response.getData().getCountry(), address.getCountry());
                                        assertEquals(response.getData().getPostalCode(), address.getPostalCode());
                                });
        }

        @Test
        void testCreateFailedInvalidToken() throws Exception {
                CreateAddressRequest request = new CreateAddressRequest();
                request.setStreet("test");
                request.setCity("test");
                request.setProvince("test");
                request.setCountry("test");
                request.setPostalCode("12345");

                mockMvc.perform(
                                post("/contact/test/address")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testCreateFailedBadRequest() throws Exception {
                CreateAddressRequest request = new CreateAddressRequest();
                request.setStreet("test");
                request.setCity("test");
                request.setProvince("test");
                request.setCountry("test");

                mockMvc.perform(
                                post("/contact/test2/address")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testCreateFailedContactNotFound() throws Exception {
                CreateAddressRequest request = new CreateAddressRequest();
                request.setStreet("test");
                request.setCity("test");
                request.setProvince("test");
                request.setCountry("test");
                request.setPostalCode("12345");

                mockMvc.perform(
                                post("/contact/test2/address")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isNotFound())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testGetSuccess() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                Address address = new Address();
                address.setId(UUID.randomUUID().toString());
                address.setStreet("test");
                address.setCity("test");
                address.setProvince("test");
                address.setCountry("test");
                address.setPostalCode("12345");
                address.setContact(contact);
                addressRepository.save(address);

                mockMvc.perform(
                                get("/contact/test/address/" + address.getId())
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getData().getId());
                                        assertNotNull(response.getData().getStreet());
                                        assertNotNull(response.getData().getCity());
                                        assertNotNull(response.getData().getProvince());
                                        assertNotNull(response.getData().getCountry());
                                        assertNotNull(response.getData().getPostalCode());
                                        assertEquals(address.getId(), response.getData().getId());
                                        assertEquals(address.getStreet(), response.getData().getStreet());
                                        assertEquals(address.getCity(), response.getData().getCity());
                                        assertEquals(address.getProvince(), response.getData().getProvince());
                                        assertEquals(address.getCountry(), response.getData().getCountry());
                                        assertEquals(address.getPostalCode(), response.getData().getPostalCode());
                                });
        }

        @Test
        void testGetFailedNotFound() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                Address address = new Address();
                address.setId(UUID.randomUUID().toString());
                address.setStreet("test");
                address.setCity("test");
                address.setProvince("test");
                address.setCountry("test");
                address.setPostalCode("12345");
                address.setContact(contact);
                addressRepository.save(address);

                mockMvc.perform(
                                get("/contact/test/address/test")
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isNotFound())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testListSuccessWithNoParams() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                for (int i = 1; i <= 3; i++) {
                        Address address = new Address();
                        address.setId(UUID.randomUUID().toString());
                        address.setStreet("test" + i);
                        address.setCity("test" + i);
                        address.setProvince("test" + i);
                        address.setCountry("test" + i);
                        address.setPostalCode("12345");
                        address.setContact(contact);
                        addressRepository.save(address);
                }

                mockMvc.perform(
                                get("/contact/test/address")
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<AddressResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getData());
                                        assertEquals(3, response.getData().size());
                                        assertEquals(null, response.getSort().getSortBy());
                                        assertEquals("asc", response.getSort().getSortDirection());
                                });
        }

        @Test
        void testListSuccessWithParams() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                for (int i = 1; i <= 3; i++) {
                        Address address = new Address();
                        address.setId(UUID.randomUUID().toString());
                        address.setStreet("test" + i);
                        address.setCity("test" + i);
                        address.setProvince("test" + i);
                        address.setCountry("test" + i);
                        address.setPostalCode("12345");
                        address.setContact(contact);
                        addressRepository.save(address);
                }

                mockMvc.perform(
                                get("/contact/test/address")
                                                .header("X-API-TOKEN", "test")
                                                .param("sortBy", "street")
                                                .param("sortDirection", "desc"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<AddressResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getData());
                                        assertEquals(3, response.getData().size());
                                        assertEquals("test3", response.getData().get(0).getStreet());
                                        assertEquals("street", response.getSort().getSortBy());
                                        assertEquals("desc", response.getSort().getSortDirection());
                                });
        }

        @Test
        void testListFailedBadRequest() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                for (int i = 1; i <= 3; i++) {
                        Address address = new Address();
                        address.setId(UUID.randomUUID().toString());
                        address.setStreet("test" + i);
                        address.setCity("test" + i);
                        address.setProvince("test" + i);
                        address.setCountry("test" + i);
                        address.setPostalCode("12345");
                        address.setContact(contact);
                        addressRepository.save(address);
                }

                mockMvc.perform(
                                get("/contact/test/address")
                                                .header("X-API-TOKEN", "test")
                                                .param("sortBy", "street")
                                                .param("sortDirection", "test"))
                                .andExpectAll(status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<List<AddressResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testUpdateSuccess() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                Address address = new Address();
                address.setId(UUID.randomUUID().toString());
                address.setStreet("test");
                address.setCity("test");
                address.setProvince("test");
                address.setCountry("test");
                address.setPostalCode("12345");
                address.setContact(contact);
                addressRepository.save(address);

                CreateAddressRequest request = new CreateAddressRequest();
                request.setStreet("test2");
                request.setCity("test2");
                request.setProvince("test2");
                request.setCountry("test2");
                request.setPostalCode("12345");

                mockMvc.perform(
                                post("/contact/test/address")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getData());

                                        Address addressDb = addressRepository.findById(response.getData().getId())
                                                        .orElse(null);
                                        assertNotNull(addressDb);
                                        assertEquals(response.getData().getStreet(), addressDb.getStreet());
                                        assertEquals(response.getData().getCity(), addressDb.getCity());
                                        assertEquals(response.getData().getProvince(), addressDb.getProvince());
                                        assertEquals(response.getData().getCountry(), addressDb.getCountry());
                                        assertEquals(response.getData().getPostalCode(), addressDb.getPostalCode());
                                });
        }

        @Test
        void testUpdateFailedBlankValue() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                Address address = new Address();
                address.setId(UUID.randomUUID().toString());
                address.setStreet("test");
                address.setCity("test");
                address.setProvince("test");
                address.setCountry("test");
                address.setPostalCode("12345");
                address.setContact(contact);
                addressRepository.save(address);

                CreateAddressRequest request = new CreateAddressRequest();
                request.setStreet("test2");
                request.setCity("test2");
                request.setProvince("test2");
                request.setCountry("test2");

                mockMvc.perform(
                                post("/contact/test/address")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());

                                        Address addressDb = addressRepository.findById(address.getId()).orElse(null);
                                        assertNotNull(addressDb);
                                        assertNotEquals(request.getStreet(), addressDb.getStreet());
                                        assertNotEquals(request.getCity(), addressDb.getCity());
                                        assertNotEquals(request.getProvince(), addressDb.getProvince());
                                        assertNotEquals(request.getCountry(), addressDb.getCountry());
                                        assertNotEquals(request.getPostalCode(), addressDb.getPostalCode());
                                });
        }

        @Test
        void testUpdateFailedNotFound() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                Address address = new Address();
                address.setId(UUID.randomUUID().toString());
                address.setStreet("test");
                address.setCity("test");
                address.setProvince("test");
                address.setCountry("test");
                address.setPostalCode("12345");
                address.setContact(contact);
                addressRepository.save(address);

                CreateAddressRequest request = new CreateAddressRequest();
                request.setStreet("test2");
                request.setCity("test2");
                request.setProvince("test2");
                request.setCountry("test2");
                request.setPostalCode("123456");

                mockMvc.perform(
                                patch("/contact/test/address/test")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isNotFound())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());

                                        Address addressDb = addressRepository.findById(address.getId()).orElse(null);
                                        assertNotNull(addressDb);
                                        assertNotEquals(request.getStreet(), addressDb.getStreet());
                                        assertNotEquals(request.getCity(), addressDb.getCity());
                                        assertNotEquals(request.getProvince(), addressDb.getProvince());
                                        assertNotEquals(request.getCountry(), addressDb.getCountry());
                                        assertNotEquals(request.getPostalCode(), addressDb.getPostalCode());
                                });
        }

        @Test
        void testDeleteSuccess() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                Address address = new Address();
                address.setId(UUID.randomUUID().toString());
                address.setStreet("test");
                address.setCity("test");
                address.setProvince("test");
                address.setCountry("test");
                address.setPostalCode("12345");
                address.setContact(contact);
                addressRepository.save(address);

                mockMvc.perform(
                                delete("/contact/test/address/" + address.getId())
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNull(response.getData());
                                        
                                        Address addressDb = addressRepository.findById(address.getId()).orElse(null);
                                        assertNull(addressDb);
                                });
        }

        @Test
        void testDeleteFailedNotFound() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                Address address = new Address();
                address.setId(UUID.randomUUID().toString());
                address.setStreet("test");
                address.setCity("test");
                address.setProvince("test");
                address.setCountry("test");
                address.setPostalCode("12345");
                address.setContact(contact);
                addressRepository.save(address);

                mockMvc.perform(
                                delete("/contact/test/address/test")
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isNotFound())
                                .andDo(result -> {
                                        WebResponse<AddressResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());
                                        
                                        Address addressDb = addressRepository.findById(address.getId()).orElse(null);
                                        assertNotNull(addressDb);
                                });
        }
}
