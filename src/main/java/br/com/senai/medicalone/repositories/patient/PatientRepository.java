package br.com.senai.medicalone.repositories.patient;

import br.com.senai.medicalone.entities.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    @Query("SELECT p FROM Patient p WHERE p.cpf = :cpf")
    Patient findByCpf(@Param("cpf") String cpf);


    @Query("SELECT p FROM Patient p WHERE p.fullName = :name")
    List<Patient> findByName(@Param("name") String name);

    @Query("SELECT p FROM Patient p WHERE p.phone = :phone")
    List<Patient> findByPhone(@Param("phone") String phone);
}