package br.com.senai.medicalone.repositories.exam;

import br.com.senai.medicalone.entities.exam.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByName(String name);
    List<Exam> findByExamDate(LocalDate examDate);
    List<Exam> findByType(String type);
    List<Exam> findByLaboratory(String laboratory);
    List<Exam> findByPatientId(Long patientId);
}