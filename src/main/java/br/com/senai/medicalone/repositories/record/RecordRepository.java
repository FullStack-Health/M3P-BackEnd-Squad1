package br.com.senai.medicalone.repositories.record;

import br.com.senai.medicalone.entities.record.Record;
import br.com.senai.medicalone.entities.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    Record findByPatient(Patient patient);
}