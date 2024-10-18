package br.com.senai.medicalone.repositories.user;

import br.com.senai.medicalone.entities.user.PreRegisterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreRegisterUserRepository extends JpaRepository<PreRegisterUser, Long> {
    Optional<PreRegisterUser> findByEmail(String email);
    boolean existsByEmail(String email);
}