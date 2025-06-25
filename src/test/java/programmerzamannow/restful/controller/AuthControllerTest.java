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

import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.models.WebResponse;
import programmerzamannow.restful.models.users.LoginUserRequest;
import programmerzamannow.restful.models.users.RegisterUserRequest;
import programmerzamannow.restful.models.users.TokenResponse;
import programmerzamannow.restful.models.users.UserResponse;
import programmerzamannow.restful.repositories.AddressRepository;
import programmerzamannow.restful.repositories.ContactRepository;
import programmerzamannow.restful.repositories.UserRepository;
import programmerzamannow.restful.repositories.UserTokenRepository;
import programmerzamannow.restful.securities.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
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
        }

        @Test
        void testRegisterSuccess() throws Exception {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setUsername("test");
                request.setPassword("test");
                request.setName("Test");
                request.setEmail("test@test.com");

                mockMvc.perform(
                                post("/auth/register")
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
                request.setEmail("");

                mockMvc.perform(
                                post("/auth/register")
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
                user.setEmail("test@test.com");
                user.setIsVerified(true);
                userRepository.save(user);

                RegisterUserRequest request = new RegisterUserRequest();
                request.setUsername("test");
                request.setPassword("test");
                request.setName("Test");
                request.setEmail("test@test.com");

                mockMvc.perform(
                                post("/auth/register")
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
                request.setUsernameOrEmail("test");
                request.setPassword("test");

                mockMvc.perform(
                                post("/auth/login")
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
                user.setEmail("test@test.com");
                user.setIsVerified(true);
                userRepository.save(user);

                LoginUserRequest loginRequest = new LoginUserRequest();
                loginRequest.setUsernameOrEmail("test");
                loginRequest.setPassword("test2");

                mockMvc.perform(
                                post("/auth/login")
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
        void testLoginSuccessUsingUsername() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setEmail("test@test.com");
                user.setIsVerified(true);
                userRepository.save(user);

                LoginUserRequest loginRequest = new LoginUserRequest();
                loginRequest.setUsernameOrEmail("test");
                loginRequest.setPassword("test");

                mockMvc.perform(
                                post("/auth/login")
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
        void testLoginSuccessUsingEmail() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
                user.setName("Test");
                user.setEmail("test@test.com");
                user.setIsVerified(true);
                userRepository.save(user);

                LoginUserRequest loginRequest = new LoginUserRequest();
                loginRequest.setUsernameOrEmail("test@test.com");
                loginRequest.setPassword("test");

                mockMvc.perform(
                                post("/auth/login")
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
                user.setEmail("test@test.com");
                user.setToken("test");
                user.setTokenExpiredAt(Instant.now().toEpochMilli() + 1000000000L);
                user.setIsVerified(true);
                userRepository.save(user);

                mockMvc.perform(
                                get("/auth/logout")
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
                user.setEmail("test@test.com");
                user.setToken("test");
                user.setTokenExpiredAt(Instant.now().toEpochMilli() + 1000000000L);
                user.setIsVerified(true);
                userRepository.save(user);

                mockMvc.perform(
                                get("/auth/logout")
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
