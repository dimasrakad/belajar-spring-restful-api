package programmerzamannow.restful.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import programmerzamannow.restful.entity.UserToken;
import programmerzamannow.restful.enums.TokenType;
import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.user.LoginUserRequest;
import programmerzamannow.restful.model.user.RegisterUserRequest;
import programmerzamannow.restful.model.user.RequestResetPasswordRequest;
import programmerzamannow.restful.model.user.ResetPasswordRequest;
import programmerzamannow.restful.model.user.TokenResponse;
import programmerzamannow.restful.repository.UserTokenRepository;
import programmerzamannow.restful.repository.UserRepository;
import programmerzamannow.restful.security.BCrypt;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private EmailService emailService;

    @Value("${frontend.address}")
    private String frontendAddress;

    @Transactional
    public void register(RegisterUserRequest request) {
        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The username is already exist");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setIsVerified(false);

        userRepository.save(user);

        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setTokenType(TokenType.EMAIL_VERIFICATION);
        userToken.setToken(UUID.randomUUID().toString());
        userToken.setExpiredAt(next30Minutes());
        userTokenRepository.save(userToken);

        String verificationLink = frontendAddress + "/verify?token=" + userToken.getToken();
        emailService.sendVerification(user.getEmail(), verificationLink);
    }

    @Transactional
    public void verifyEmail(String token) {
        UserToken userToken = userTokenRepository.findFirstByTokenTypeAndToken(TokenType.EMAIL_VERIFICATION, token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is invalid"));
        
        if (userToken.getExpiredAt() < Instant.now().toEpochMilli()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is expired");
        }

        userToken.setToken(null);
        userToken.setExpiredAt(null);
        userTokenRepository.save(userToken);

        User user = userToken.getUser();
        user.setIsVerified(true);
        userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findFirstByUsernameOrEmail(request.getUsernameOrEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                "Username/email or password is wrong"));

        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username/email or password is wrong");
        }
    }

    @Transactional
    public void requestResetPassword(RequestResetPasswordRequest request) {
        validationService.validate(request);

        User user = userRepository.findFirstByEmail(request.getEmail()).orElse(null);

        if (user != null) {
            UserToken userToken = userTokenRepository.findFirstByUserAndTokenType(user, TokenType.PASSWORD_RESET).orElse(null);

            if (userToken == null) {
                userToken = new UserToken();
                userToken.setUser(user);
                userToken.setTokenType(TokenType.PASSWORD_RESET);
            }

            userToken.setToken(UUID.randomUUID().toString());
            userToken.setExpiredAt(next30Minutes());

            userTokenRepository.save(userToken);

            String resetLink = frontendAddress + "/auth/reset-password?token=" + userToken.getToken();

            emailService.sendPasswordReset(user.getEmail(), resetLink);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        validationService.validate(request);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New Password and confirm password is not match");
        }

        UserToken userToken = userTokenRepository.findFirstByTokenTypeAndToken(TokenType.PASSWORD_RESET, request.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is invalid"));

        if (userToken.getExpiredAt() < Instant.now().toEpochMilli()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is expired");
        }

        userToken.setToken(null);
        userToken.setExpiredAt(null);
        userTokenRepository.save(userToken);

        User user = userToken.getUser();
        user.setPassword(BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt()));
        userRepository.save(user);
    }

    @Transactional
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);
        userRepository.save(user);
    }

    private Long next30Days() {
        return Instant.now().plusMillis(1000L * 60 * 60 * 24 * 30).toEpochMilli();
    }

    private Long next30Minutes() {
        return Instant.now().plusMillis(1000L * 60 * 30).toEpochMilli();
    }
}
