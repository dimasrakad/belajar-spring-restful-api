package programmerzamannow.restful.repositories;

import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import programmerzamannow.restful.entities.Address;
import programmerzamannow.restful.entities.Contact;

import java.util.List;


@Repository
public interface AddressRepository extends JpaRepository<Address, String>, JpaSpecificationExecutor<Address> {
    Optional<Address> findFirstByContactAndId(Contact contact, String id);
    List<Address> findByContact(Contact contact, Sort sort);
}
