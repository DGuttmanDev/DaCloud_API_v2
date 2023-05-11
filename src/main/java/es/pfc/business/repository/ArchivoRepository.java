package es.pfc.business.repository;

import es.pfc.business.model.Archivo;
import es.pfc.business.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivoRepository extends JpaRepository<Archivo, Long> {

    Archivo findArchivoByNombreAndUser(String nombre, User user);

    boolean existsByNombreAndUser(String nombre, User user);

}
