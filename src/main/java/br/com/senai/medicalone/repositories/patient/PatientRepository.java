package br.com.senai.medicalone.repositories.patient;

import br.com.senai.medicalone.entities.patient.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByPhone(String phone);

    @Query("SELECT p FROM Patient p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Patient> findByName(@Param("name") String name);

    @Query("SELECT p FROM Patient p WHERE p.cpf = :cpf")
    Patient findByCpf(@Param("cpf") String cpf);

    @Query("SELECT p FROM Patient p WHERE p.phone = :phone")
    List<Patient> findByPhone(@Param("phone") String phone);

    @Query("SELECT p FROM Patient p WHERE p.email = :email")
    Patient findByEmail(@Param("email") String email);

    @Query(
            "SELECT p FROM Patient p " +
                    "WHERE (:searchTerm IS NULL OR " +

                    "p.fullName ILIKE %:searchTerm% OR " +
                    "p.phone ILIKE %:searchTerm% OR " +
                    "p.email ILIKE %:searchTerm%)"
    )
    Page<Patient> findByFilter(
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}