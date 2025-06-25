package programmerzamannow.restful.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import programmerzamannow.restful.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findFirstByUsernameOrEmail(String usernameOrEmail);

    Optional<User> findFirstByEmail(String email);

    Optional<User> findFirstByToken(String token);
}
