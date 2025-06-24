package programmerzamannow.restful.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import programmerzamannow.restful.entity.UserToken;
import programmerzamannow.restful.enums.TokenType;
import programmerzamannow.restful.entity.User;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findFirstByTokenTypeAndToken(TokenType tokenType, String token);
    Optional<UserToken> findFirstByUserAndTokenType(User user, TokenType tokenType);
}
