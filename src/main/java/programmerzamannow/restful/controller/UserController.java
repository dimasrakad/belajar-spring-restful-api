package programmerzamannow.restful.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.UpdateUserRequest;
import programmerzamannow.restful.model.UserResponse;
import programmerzamannow.restful.model.WebResponse;
import programmerzamannow.restful.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
public class UserController {
    final String path = "/api/user";

    @Autowired
    private UserService userService;

    @GetMapping(path + "/current")
    public WebResponse<UserResponse> getCurrent(User user) {
        UserResponse response = userService.getCurrent(user);

        return WebResponse.<UserResponse>builder().data(response).build();
    }
    
    @PatchMapping(path + "/current")
    public WebResponse<UserResponse> updateCurrent(User user, @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateCurrent(user, request);

        return WebResponse.<UserResponse>builder().data(response).build();
    }
}
