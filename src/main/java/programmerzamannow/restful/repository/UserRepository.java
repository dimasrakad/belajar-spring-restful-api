package programmerzamannow.restful.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import programmerzamannow.restful.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public Optional<User> findFirstByToken(String token);
}
