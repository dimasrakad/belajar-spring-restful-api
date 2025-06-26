package programmerzamannow.restful.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import programmerzamannow.restful.entities.ContactCategory;
import programmerzamannow.restful.entities.User;

@Repository
public interface ContactCategoryRepository extends JpaRepository<ContactCategory, Long> {
    List<ContactCategory> findByUser(User user);
    Optional<ContactCategory> findFirstByUserAndId(User user, Long id);
}
