package cl.commons.ms.admin.usuario.be.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.commons.ms.admin.usuario.be.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>{

    Optional<User> findByEmail(String email);
    Optional<User> findByToken(String token);
}
