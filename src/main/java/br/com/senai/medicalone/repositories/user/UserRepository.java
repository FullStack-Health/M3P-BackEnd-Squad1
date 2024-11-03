package br.com.senai.medicalone.repositories.user;

import br.com.senai.medicalone.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<User> findByEmailOrCpf(String email, String cpf);
    void delete(User user);
    boolean existsByPhone(String phone);


    @Query("SELECT u FROM User u WHERE (:id IS NULL OR u.id = :id) AND (:name IS NULL OR u.name LIKE %:name%) AND (:email IS NULL OR u.email LIKE %:email%)")
    Page<User> findByIdOrNameOrEmail(@Param("id") Long id, @Param("name") String name, @Param("email") String email, Pageable pageable);
}