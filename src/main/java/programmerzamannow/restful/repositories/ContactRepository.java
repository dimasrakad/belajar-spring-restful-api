package programmerzamannow.restful.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import programmerzamannow.restful.entities.Contact;
import programmerzamannow.restful.entities.ContactCategory;
import programmerzamannow.restful.entities.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String>, JpaSpecificationExecutor<Contact> {
    Optional<Contact> findFirstByUserAndId(User user, String id);
    List<Contact> findAllByUserAndIdIn(User user, List<String> ids);
    List<Contact> findAllByUserAndContactCategory(User user, ContactCategory contactCategory);
}
