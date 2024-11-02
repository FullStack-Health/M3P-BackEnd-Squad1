package br.com.senai.medicalone.repositories.exam;

import br.com.senai.medicalone.entities.exam.Exam;
import br.com.senai.medicalone.entities.patient.Patient;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ExamRepositoryTest {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Exam exam;
    private Patient patient;

    @BeforeEach
    public void setUp() {
        examRepository.deleteAll();
        patientRepository.deleteAll();

        patient = createAndSavePatient();

        exam = new Exam();
        exam.setName("Blood Test");
        exam.setPatient(patient);
        exam.setExamDate(LocalDate.now());
        exam.setExamTime(LocalTime.now());
        exam.setType("Sangue");
        exam.setLaboratory("Laboratório XYZ");
    }

    private Patient createAndSavePatient() {
        Patient patient = new Patient();
        patient.setFullName("John Doe");
        patient.setGender("Masculino");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setCpf("12345678900");
        patient.setRg("1234567890");
        patient.setRgIssuer("SSP");
        patient.setMaritalStatus("Solteiro");
        patient.setPhone("99999999999");
        patient.setEmail("user@example.com");
        patient.setPlaceOfBirth("São Paulo");
        patient.setEmergencyContact("99999999999");
        patient.setAllergies(List.of("Pólen"));
        patient.setSpecificCare(List.of("Cuidados Especiais"));
        patient.setHealthInsurance("Unimed");
        patient.setHealthInsuranceNumber("1234567890");
        patient.setHealthInsuranceValidity(LocalDate.of(2025, 12, 31));
        patient.setZipCode("12345-678");
        patient.setCity("São Paulo");
        patient.setState("SP");
        patient.setStreet("Rua Exemplo");
        patient.setNumber("123");
        patient.setComplement("Apto 101");
        patient.setNeighborhood("Centro");
        patient.setReferencePoint("Próximo ao mercado");
        patient.setPassword("password");
        return patientRepository.save(patient);
    }

    @Test
    public void testFindByName() {
        examRepository.save(exam);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Exam> exams = examRepository.findByName("Blood Test", pageable);
        assertThat(exams).isNotEmpty();
        assertThat(exams.getContent().get(0).getName()).isEqualTo("Blood Test");
    }

    @Test
    public void testFindByPatientId() {
        examRepository.save(exam);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Exam> exams = examRepository.findByPatientId(patient.getId(), pageable);
        assertThat(exams).isNotEmpty();
        assertThat(exams.getContent().get(0).getPatient().getId()).isEqualTo(patient.getId());
    }

    @Test
    public void testSaveExam() {
        Exam savedExam = examRepository.save(exam);
        assertThat(savedExam).isNotNull();
        assertThat(savedExam.getId()).isNotNull();
    }

    @Test
    public void testDeleteExam() {
        Exam savedExam = examRepository.save(exam);
        examRepository.deleteById(savedExam.getId());
        boolean exists = examRepository.existsById(savedExam.getId());
        assertThat(exists).isFalse();
    }
}