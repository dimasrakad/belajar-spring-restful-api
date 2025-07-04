package programmerzamannow.restful.resolvers;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.repositories.UserRepository;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
            NativeWebRequest nativeWebRequest,
            WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        String token = httpServletRequest.getHeader("X-API-TOKEN");

        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No token provided");
        }

        User user = userRepository.findFirstByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No token provided"));

        if (user.getTokenExpiredAt() < Instant.now().toEpochMilli()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
        }

        return user;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }

}
