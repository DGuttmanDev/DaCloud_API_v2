package es.pfc.business.repository;

import es.pfc.business.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByMail(String mail);
    User findByNick(String nick);

    boolean existsByMail(String mail);
    boolean existsByNick(String nick);

}
