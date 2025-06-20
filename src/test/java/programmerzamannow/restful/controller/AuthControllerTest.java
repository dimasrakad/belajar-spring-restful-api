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

import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.LoginUserRequest;
import programmerzamannow.restful.model.RegisterUserRequest;
import programmerzamannow.restful.model.TokenResponse;
import programmerzamannow.restful.model.UserResponse;
import programmerzamannow.restful.model.WebResponse;
import programmerzamannow.restful.repository.AddressRepository;
import programmerzamannow.restful.repository.ContactRepository;
import programmerzamannow.restful.repository.UserRepository;
import programmerzamannow.restful.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
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
        void testRegisterSuccess() throws Exception {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setUsername("test");
                request.setPassword("test");
                request.setName("Test");

                mockMvc.perform(
                                post("/api/auth/register")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertEquals(null, response.getData());
                                });
        }

        @Test
        void testRegisterFailedBadRequest() throws Exception {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setUsername("");
                request.setPassword("");
                request.setName("");

                mockMvc.perform(
                                post("/api/auth/register")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testRegisterFailedDuplicate() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                userRepository.save(user);

                RegisterUserRequest request = new RegisterUserRequest();
                request.setUsername("test");
                request.setPassword("test");
                request.setName("Test");

                mockMvc.perform(
                                post("/api/auth/register")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getError());
                                });
        }

        @Test
        void testLoginFailedUserNotFound() throws Exception {
                LoginUserRequest request = new LoginUserRequest();
                request.setUsername("test");
                request.setPassword("test");

                mockMvc.perform(
                                post("/api/auth/login")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
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
        void testLoginFailedWrongPassword() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                userRepository.save(user);

                LoginUserRequest loginRequest = new LoginUserRequest();
                loginRequest.setUsername("test");
                loginRequest.setPassword("test2");

                mockMvc.perform(
                                post("/api/auth/login")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(loginRequest)))
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
        void testLoginSuccess() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                userRepository.save(user);

                LoginUserRequest loginRequest = new LoginUserRequest();
                loginRequest.setUsername("test");
                loginRequest.setPassword("test");

                mockMvc.perform(
                                post("/api/auth/login")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<TokenResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {

                                                        });

                                        assertNotNull(response.getData().getToken());
                                        assertNotNull(response.getData().getExpiredAt());

                                        User userDb = userRepository.findById("test").orElse(null);
                                        assertNotNull(userDb);
                                        assertEquals(userDb.getToken(), response.getData().getToken());
                                        assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
                                });
        }

        @Test
        void testLogoutSuccess() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setToken("test");
                user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
                userRepository.save(user);

                mockMvc.perform(
                                get("/api/auth/logout")
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNull(response.getData());

                                        User userDb = userRepository.findById("test").orElse(null);

                                        assertNotNull(userDb);
                                        assertNull(userDb.getToken());
                                        assertNull(userDb.getTokenExpiredAt());
                                });
        }

        @Test
        void testLogoutFailed() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setToken("test");
                user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
                userRepository.save(user);

                mockMvc.perform(
                                get("/api/auth/logout")
                                                .header("X-API-TOKEN", "test2"))
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
