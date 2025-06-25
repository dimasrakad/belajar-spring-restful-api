package programmerzamannow.restful.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import programmerzamannow.restful.entities.Contact;
import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.models.WebResponse;
import programmerzamannow.restful.models.contacts.ContactResponse;
import programmerzamannow.restful.models.contacts.CreateContactRequest;
import programmerzamannow.restful.models.contacts.UpdateContactRequest;
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
public class ContactControllerTest {
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
        }

        @Test
        void testCreateSuccess() throws Exception {
                CreateContactRequest request = new CreateContactRequest();
                request.setFirstName("test");
                request.setLastName("test");
                request.setEmail("test@test.com");
                request.setPhone("+628123456789");

                mockMvc.perform(
                                post("/contact")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getData());

                                        Contact contact = contactRepository.findById(response.getData().getId())
                                                        .orElse(null);
                                        assertNotNull(contact);
                                        assertEquals(response.getData().getFirstName(), contact.getFirstName());
                                        assertEquals(response.getData().getLastName(), contact.getLastName());
                                        assertEquals(response.getData().getEmail(), contact.getEmail());
                                        assertEquals(response.getData().getPhone(), contact.getPhone());
                                });
        }

        @Test
        void testCreateSuccessUsingBlankValue() throws Exception {
                CreateContactRequest request = new CreateContactRequest();
                request.setFirstName("test");

                mockMvc.perform(
                                post("/contact")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getData());

                                        Contact contact = contactRepository.findById(response.getData().getId())
                                                        .orElse(null);
                                        assertNotNull(contact);
                                        assertEquals(response.getData().getFirstName(), contact.getFirstName());
                                        assertEquals(response.getData().getLastName(), contact.getLastName());
                                        assertEquals(response.getData().getEmail(), contact.getEmail());
                                        assertEquals(response.getData().getPhone(), contact.getPhone());
                                });
        }

        @Test
        void testCreateFailedInvalidToken() throws Exception {
                CreateContactRequest request = new CreateContactRequest();
                request.setFirstName("test");

                mockMvc.perform(
                                post("/contact")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test2"))
                                .andExpectAll(status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testCreateFailedBadRequest() throws Exception {
                CreateContactRequest request = new CreateContactRequest();
                request.setFirstName("test");
                request.setLastName("test");
                request.setEmail("test");
                request.setPhone("628123456789");

                mockMvc.perform(
                                post("/contact")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testGetSuccess() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setFirstName("test");
                contact.setLastName("test");
                contact.setEmail("test@test.com");
                contact.setPhone("+628123456789");
                contact.setUser(user);
                contactRepository.save(contact);

                mockMvc.perform(
                                get("/contact/" + contact.getId())
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getData().getId());
                                        assertNotNull(response.getData().getFirstName());
                                        assertNotNull(response.getData().getLastName());
                                        assertNotNull(response.getData().getEmail());
                                        assertNotNull(response.getData().getPhone());
                                        assertEquals(contact.getId(), response.getData().getId());
                                        assertEquals(contact.getFirstName(), response.getData().getFirstName());
                                        assertEquals(contact.getLastName(), response.getData().getLastName());
                                        assertEquals(contact.getEmail(), response.getData().getEmail());
                                        assertEquals(contact.getPhone(), response.getData().getPhone());
                                });
        }

        @Test
        void testGetFailedNotFound() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setFirstName("test");
                contact.setLastName("test");
                contact.setEmail("test@test.com");
                contact.setPhone("+628123456789");
                contact.setUser(user);
                contactRepository.save(contact);

                mockMvc.perform(
                                get("/contact/test")
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isNotFound())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testUpdateSuccess() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setFirstName("test");
                contact.setLastName("test");
                contact.setEmail("test@test.com");
                contact.setPhone("+628123456789");
                contact.setUser(user);
                contactRepository.save(contact);

                UpdateContactRequest request = new UpdateContactRequest();
                request.setFirstName("test2");
                request.setLastName("test2");
                request.setEmail("test2@test.com");
                request.setPhone("+628123456780");

                mockMvc.perform(
                                patch("/contact/" + contact.getId())
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getData().getId());
                                        assertNotNull(response.getData().getFirstName());
                                        assertNotNull(response.getData().getLastName());
                                        assertNotNull(response.getData().getEmail());
                                        assertNotNull(response.getData().getPhone());

                                        Contact contactDb = contactRepository.findById(response.getData().getId())
                                                        .orElse(null);
                                        assertNotNull(contactDb);
                                        assertEquals(request.getFirstName(), contactDb.getFirstName());
                                        assertEquals(request.getLastName(), contactDb.getLastName());
                                        assertEquals(request.getEmail(), contactDb.getEmail());
                                        assertEquals(request.getPhone(), contactDb.getPhone());
                                });
        }

        @Test
        void testUpdateSuccessUsingBlankValue() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setFirstName("test");
                contact.setLastName("test");
                contact.setEmail("test@test.com");
                contact.setPhone("+628123456789");
                contact.setUser(user);
                contactRepository.save(contact);

                UpdateContactRequest request = new UpdateContactRequest();
                request.setFirstName("test2");

                mockMvc.perform(
                                patch("/contact/" + contact.getId())
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getData().getId());
                                        assertNotNull(response.getData().getFirstName());
                                        assertNotNull(response.getData().getLastName());
                                        assertNotNull(response.getData().getEmail());
                                        assertNotNull(response.getData().getPhone());

                                        Contact contactDb = contactRepository.findById(response.getData().getId())
                                                        .orElse(null);
                                        assertNotNull(contactDb);
                                        assertEquals(request.getFirstName(), contactDb.getFirstName());
                                        assertEquals(contact.getLastName(), contactDb.getLastName());
                                        assertEquals(contact.getEmail(), contactDb.getEmail());
                                        assertEquals(contact.getPhone(), contactDb.getPhone());
                                });
        }

        @Test
        void testUpdateFailedBadRequest() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setFirstName("test");
                contact.setLastName("test");
                contact.setEmail("test@test.com");
                contact.setPhone("+628123456789");
                contact.setUser(user);
                contactRepository.save(contact);

                UpdateContactRequest request = new UpdateContactRequest();
                request.setFirstName("test2");
                request.setLastName("test2");
                request.setEmail("test2");
                request.setPhone("628123456780");

                mockMvc.perform(
                                patch("/contact/" + contact.getId())
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getError());

                                        Contact contactDb = contactRepository.findById(contact.getId()).orElse(null);
                                        assertNotNull(contactDb);
                                        assertEquals(contact.getFirstName(), contactDb.getFirstName());
                                        assertEquals(contact.getLastName(), contactDb.getLastName());
                                        assertEquals(contact.getEmail(), contactDb.getEmail());
                                        assertEquals(contact.getPhone(), contactDb.getPhone());
                                });
        }

        @Test
        void testSearchSuccessNoParams() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                for (int i = 1; i <= 15; i++) {
                        Contact contact = new Contact();
                        contact.setId(UUID.randomUUID().toString());
                        contact.setFirstName("test" + i);
                        contact.setLastName("test" + i);
                        contact.setEmail("test" + i + "@test.com");
                        contact.setPhone("+62812345678" + i);
                        contact.setUser(user);
                        contactRepository.save(contact);
                }

                mockMvc.perform(
                                get("/contact")
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getData());
                                        assertEquals(10, response.getData().size());
                                        assertEquals(0, response.getPagination().getCurrentPage());
                                        assertEquals(2, response.getPagination().getTotalPage());
                                        assertEquals(10, response.getPagination().getSize());
                                        assertEquals(null, response.getSort().getSortBy());
                                        assertEquals("asc", response.getSort().getSortDirection());
                                });
        }

        @Test
        void testSearchSuccessWithParams() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                for (int i = 1; i <= 15; i++) {
                        Contact contact = new Contact();
                        contact.setId(UUID.randomUUID().toString());
                        contact.setFirstName("test" + i);
                        contact.setLastName("test" + i);
                        contact.setEmail("test" + i + "@test.com");
                        contact.setPhone("+62812345678" + i);
                        contact.setUser(user);
                        contactRepository.save(contact);
                }

                mockMvc.perform(
                                get("/contact")
                                                .header("X-API-TOKEN", "test")
                                                .param("name", "test1")
                                                .param("page", "1")
                                                .param("size", "5")
                                                .param("sortBy", "firstName")
                                                .param("sortDirection", "desc"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getData());
                                        assertEquals(2, response.getData().size()); // page 1 only contains 2 contacts
                                        assertEquals(response.getData().get(0).getFirstName(), "test10");
                                        assertEquals(1, response.getPagination().getCurrentPage());
                                        assertEquals(2, response.getPagination().getTotalPage());
                                        assertEquals(5, response.getPagination().getSize());
                                        assertEquals("firstName", response.getSort().getSortBy());
                                        assertEquals("desc", response.getSort().getSortDirection());
                                });
        }

        @Test
        void testSearchFailedBadRequest() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                for (int i = 1; i <= 15; i++) {
                        Contact contact = new Contact();
                        contact.setId(UUID.randomUUID().toString());
                        contact.setFirstName("test" + i);
                        contact.setLastName("test" + i);
                        contact.setEmail("test" + i + "@test.com");
                        contact.setPhone("+62812345678" + i);
                        contact.setUser(user);
                        contactRepository.save(contact);
                }

                mockMvc.perform(
                                get("/contact")
                                                .header("X-API-TOKEN", "test")
                                                .param("name", "test1")
                                                .param("sortDirection", "test"))
                                .andExpectAll(status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testSearchFailedNotFound() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                for (int i = 1; i <= 15; i++) {
                        Contact contact = new Contact();
                        contact.setId(UUID.randomUUID().toString());
                        contact.setFirstName("test" + i);
                        contact.setLastName("test" + i);
                        contact.setEmail("test" + i + "@test.com");
                        contact.setPhone("+62812345678" + i);
                        contact.setUser(user);
                        contactRepository.save(contact);
                }

                mockMvc.perform(
                                get("/contact")
                                                .header("X-API-TOKEN", "test")
                                                .param("name", "test0"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getData());
                                        assertEquals(0, response.getData().size());
                                        assertEquals(0, response.getPagination().getCurrentPage());
                                        assertEquals(0, response.getPagination().getTotalPage());
                                        assertEquals(10, response.getPagination().getSize());
                                        assertEquals(null, response.getSort().getSortBy());
                                        assertEquals("asc", response.getSort().getSortDirection());
                                });
        }

        @Test
        void testDeleteSuccess() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setFirstName("test");
                contact.setLastName("test");
                contact.setEmail("test@test.com");
                contact.setPhone("+628123456789");
                contact.setUser(user);
                contactRepository.save(contact);

                mockMvc.perform(
                                delete("/contact/" + contact.getId())
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNull(response.getData());

                                        Contact contactDb = contactRepository.findById(contact.getId()).orElse(null);
                                        assertNull(contactDb);
                                });
        }

        @Test
        void testDeleteFailedNotFound() throws Exception {
                User user = userRepository.findById("test").orElseThrow();

                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setFirstName("test");
                contact.setLastName("test");
                contact.setEmail("test@test.com");
                contact.setPhone("+628123456789");
                contact.setUser(user);
                contactRepository.save(contact);

                mockMvc.perform(
                                delete("/contact/test")
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isNotFound())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getError());

                                        Contact contactDb = contactRepository.findById(contact.getId()).orElse(null);
                                        assertNotNull(contactDb);
                                });
        }
}
