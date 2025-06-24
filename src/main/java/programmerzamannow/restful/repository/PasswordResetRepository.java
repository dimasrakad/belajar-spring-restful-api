package programmerzamannow.restful.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import programmerzamannow.restful.entity.PasswordReset;
import programmerzamannow.restful.entity.User;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, String> {
    Optional<PasswordReset> findFirstByToken(String token);
    Optional<PasswordReset> findFirstByUser(User user);
}
