package br.com.senai.medicalone.repositories.patient;

import br.com.senai.medicalone.entities.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByFullName(String fullName);


    List<Patient> findByGender(String gender);

    Patient findByCpf(String cpf);

    @Query("SELECT p FROM Patient p WHERE p.maritalStatus = :maritalStatus")
    List<Patient> findByMaritalStatus(@Param("maritalStatus") String maritalStatus);

    @Query("SELECT p FROM Patient p WHERE p.placeOfBirth = :placeOfBirth")
    List<Patient> findByPlaceOfBirth(@Param("placeOfBirth") String placeOfBirth);
}