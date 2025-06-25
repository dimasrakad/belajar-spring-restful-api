package programmerzamannow.restful.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import programmerzamannow.restful.entities.User;
import programmerzamannow.restful.entities.UserToken;
import programmerzamannow.restful.enums.TokenType;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findFirstByTokenTypeAndToken(TokenType tokenType, String token);
    Optional<UserToken> findFirstByUserAndTokenType(User user, TokenType tokenType);
}
