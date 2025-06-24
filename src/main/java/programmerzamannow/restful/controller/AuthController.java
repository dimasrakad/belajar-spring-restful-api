package programmerzamannow.restful.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.WebResponse;
import programmerzamannow.restful.model.user.LoginUserRequest;
import programmerzamannow.restful.model.user.RegisterUserRequest;
import programmerzamannow.restful.model.user.RequestResetPasswordRequest;
import programmerzamannow.restful.model.user.ResetPasswordRequest;
import programmerzamannow.restful.model.user.TokenResponse;
import programmerzamannow.restful.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping(path = "/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        authService.register(request);

        return WebResponse.<String>builder().data(null).build();
    }

    @GetMapping("/verify")
    public WebResponse<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        
        return WebResponse.<String>builder().data(null).build();
    }
    

    @PostMapping("/login")
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
        TokenResponse response = authService.login(request);

        return WebResponse.<TokenResponse>builder().data(response).build();
    }

    @PostMapping("/request-reset-password")
    public WebResponse<String> requestResetPassword(@RequestBody RequestResetPasswordRequest request) {
        authService.requestResetPassword(request);
        
        return WebResponse.<String>builder().data(null).build();
    }

    @PostMapping("/reset-password")
    public WebResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        
        return WebResponse.<String>builder().data(null).build();
    }

    @GetMapping("/logout")
    public WebResponse<String> logout(User user) {
        authService.logout(user);

        return WebResponse.<String>builder().data(null).build();
    }

}
