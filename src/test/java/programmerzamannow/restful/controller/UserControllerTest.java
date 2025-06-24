package programmerzamannow.restful.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;

import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.WebResponse;
import programmerzamannow.restful.model.user.UpdateUserRequest;
import programmerzamannow.restful.model.user.UserResponse;
import programmerzamannow.restful.repository.AddressRepository;
import programmerzamannow.restful.repository.ContactRepository;
import programmerzamannow.restful.repository.UserRepository;
import programmerzamannow.restful.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
        @Autowired
        private MockMvc mockMvc;

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
                addressRepository.deleteAll();
                contactRepository.deleteAll();
                userRepository.deleteAll();
        }

        @Test
        void testGetCurrentUserSuccess() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setToken("test");
                user.setEmail("test@test.com");
                user.setTokenExpiredAt(Instant.now().toEpochMilli() + 1000000000L);
                userRepository.save(user);

                mockMvc.perform(
                                get("/user/current")
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<UserResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getData().getName());
                                        assertNotNull(response.getData().getUsername());
                                });
        }

        @Test
        void testGetCurrentUserFailedNoToken() throws Exception {
                mockMvc.perform(
                                get("/user/current"))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testGetCurrentUserFailedTokenInvalid() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setEmail("test@test.com");
                user.setToken("test");
                user.setTokenExpiredAt(Instant.now().toEpochMilli() + 1000000000L);
                userRepository.save(user);

                mockMvc.perform(
                                get("/user/current")
                                                .header("X-API-TOKEN", "test2"))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testGetCurrentUserFailedTokenExpired() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setEmail("test@test.com");
                user.setToken("test");
                user.setTokenExpiredAt(Instant.now().toEpochMilli() - 1000000000L);
                userRepository.save(user);

                mockMvc.perform(
                                get("/user/current")
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testUpdateCurrentUserSuccess() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setEmail("test@test.com");
                user.setToken("test");
                user.setTokenExpiredAt(Instant.now().toEpochMilli() + 1000000000L);
                userRepository.save(user);

                UpdateUserRequest request = new UpdateUserRequest();
                request.setName("Test2");
                request.setPassword("test2");

                mockMvc.perform(
                                patch("/user/current")
                                                .header("X-API-TOKEN", "test")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<UserResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getData().getName());
                                        assertNotNull(response.getData().getUsername());
                                        assertEquals("Test2", response.getData().getName());
                                        assertEquals("test", response.getData().getUsername());

                                        User userDb = userRepository.findById(response.getData().getUsername()).orElse(null);

                                        assertNotNull(userDb);
                                        assertTrue(BCrypt.checkpw("test2", userDb.getPassword()));
                                });
        }

        @Test
        void testUpdateCurrentUserSuccessBlankValue() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setEmail("test@test.com");
                user.setToken("test");
                user.setTokenExpiredAt(Instant.now().toEpochMilli() + 1000000000L);
                userRepository.save(user);

                UpdateUserRequest request = new UpdateUserRequest();
                request.setName("Test2");

                mockMvc.perform(
                                patch("/user/current")
                                                .header("X-API-TOKEN", "test")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<UserResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getData().getName());
                                        assertNotNull(response.getData().getUsername());
                                        assertEquals("Test2", response.getData().getName());
                                        assertEquals("test", response.getData().getUsername());

                                        User userDb = userRepository.findById(response.getData().getUsername()).orElse(null);

                                        assertNotNull(userDb);
                                        assertTrue(BCrypt.checkpw("test", userDb.getPassword()));
                                });
        }

        @Test
        void testUpdateCurrentUserFailed() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setEmail("test@test.com");
                user.setToken("test");
                user.setTokenExpiredAt(Instant.now().toEpochMilli() + 1000000000L);
                userRepository.save(user);

                UpdateUserRequest request = new UpdateUserRequest();
                request.setName("Test2");

                mockMvc.perform(
                                patch("/user/current")
                                                .header("X-API-TOKEN", "test2")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<UserResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getError());
                                });
        }
}
