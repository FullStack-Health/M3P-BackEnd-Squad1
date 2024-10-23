package br.com.senai.medicalone.repositories.user;

import br.com.senai.medicalone.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<User> findByEmailOrCpf(String email, String cpf);
}