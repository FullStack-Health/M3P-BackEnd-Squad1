package br.com.senai.medicalone.repositories.exam;

import br.com.senai.medicalone.entities.exam.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Page<Exam> findByName(String name, Pageable pageable);
    Page<Exam> findByPatientId(Long patientId, Pageable pageable);
    boolean existsByPatientIdAndExamDateAndExamTime(Long patientId, LocalDate examDate, LocalTime examTime);
}