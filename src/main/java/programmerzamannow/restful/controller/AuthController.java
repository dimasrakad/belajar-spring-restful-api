package programmerzamannow.restful.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.LoginUserRequest;
import programmerzamannow.restful.model.RegisterUserRequest;
import programmerzamannow.restful.model.TokenResponse;
import programmerzamannow.restful.model.WebResponse;
import programmerzamannow.restful.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AuthController {
    final String path = "/api/auth";

    @Autowired
    private AuthService authService;

    @PostMapping(path + "/register")
    public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        authService.register(request);

        return WebResponse.<String>builder().data(null).build();
    }

    @PostMapping(path + "/login")
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
        TokenResponse response = authService.login(request);

        return WebResponse.<TokenResponse>builder().data(response).build();
    }

    @GetMapping(path + "/logout")
    public WebResponse<String> logout(User user) {
        authService.logout(user);

        return WebResponse.<String>builder().data(null).build();
    }

}
