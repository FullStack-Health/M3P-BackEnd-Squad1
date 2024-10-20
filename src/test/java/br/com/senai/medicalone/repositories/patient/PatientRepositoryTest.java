package br.com.senai.medicalone.repositories.patient;

import br.com.senai.medicalone.entities.patient.Address;
import br.com.senai.medicalone.entities.patient.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    private Patient patient;

    @BeforeEach
    public void setUp() {
        patientRepository.deleteAll();

        Address address = new Address("12345-678", "São Paulo", "SP", "Rua Exemplo", "123", "Apto 101", "Centro", "Próximo ao mercado");
        patient = new Patient(null, "John Doe", "Masculino", LocalDate.of(1990, 1, 1), "123.456.789-00", "1234567890", "SSP", "Solteiro", "99999999999", "user@example.com", "São Paulo", "99999999999", List.of("Pólen"), List.of("Cuidados Especiais"), "Unimed", "1234567890", LocalDate.of(2025, 12, 31), address, "password");
    }

    @Test
    public void testSavePatient() {
        Patient savedPatient = patientRepository.save(patient);
        assertThat(savedPatient).isNotNull();
        assertThat(savedPatient.getId()).isNotNull();
    }

    @Test
    public void testFindById() {
        Patient savedPatient = patientRepository.save(patient);
        Patient foundPatient = patientRepository.findById(savedPatient.getId()).orElse(null);
        assertThat(foundPatient).isNotNull();
        assertThat(foundPatient.getId()).isEqualTo(savedPatient.getId());
    }

    @Test
    public void testExistsByEmail() {
        patientRepository.save(patient);
        boolean exists = patientRepository.existsByEmail("user@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    public void testExistsByCpf() {
        patient.setCpf(patient.cleanString(patient.getCpf()));
        patientRepository.save(patient);
        boolean exists = patientRepository.existsByCpf("12345678900");
        assertThat(exists).isTrue();
    }
    @Test
    public void testDeletePatient() {
        Patient savedPatient = patientRepository.save(patient);
        patientRepository.deleteById(savedPatient.getId());
        boolean exists = patientRepository.existsById(savedPatient.getId());
        assertThat(exists).isFalse();
    }
}