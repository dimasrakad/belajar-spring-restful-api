package programmerzamannow.restful.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.WebResponse;
import programmerzamannow.restful.model.user.UpdateUserRequest;
import programmerzamannow.restful.model.user.UserResponse;
import programmerzamannow.restful.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public WebResponse<UserResponse> getCurrent(User user) {
        UserResponse response = userService.getCurrent(user);

        return WebResponse.<UserResponse>builder().data(response).build();
    }
    
    @PatchMapping("/current")
    public WebResponse<UserResponse> updateCurrent(User user, @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateCurrent(user, request);

        return WebResponse.<UserResponse>builder().data(response).build();
    }
}
