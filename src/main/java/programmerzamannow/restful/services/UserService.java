package programmerzamannow.restful.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.models.users.UpdateUserRequest;
import programmerzamannow.restful.models.users.UserResponse;
import programmerzamannow.restful.repositories.UserRepository;
import programmerzamannow.restful.securities.BCrypt;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ValidationService validationService;

    public UserResponse getCurrent(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse updateCurrent(User user, UpdateUserRequest request) {
        validationService.validate(request);

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getPassword() != null) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }
}
