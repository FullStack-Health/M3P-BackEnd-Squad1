package br.com.senai.medicalone.repositories.patient;

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

        patient = Patient.builder()
                .id(null)
                .fullName("John Doe")
                .gender("Masculino")
                .birthDate(LocalDate.of(1990, 1, 1))
                .cpf("123.456.789-00")
                .rg("1234567890")
                .rgIssuer("SSP")
                .maritalStatus("Solteiro")
                .phone("99999999999")
                .email("user@example.com")
                .placeOfBirth("São Paulo")
                .emergencyContact("99999999999")
                .allergies(List.of("Pólen"))
                .specificCare(List.of("Cuidados Especiais"))
                .healthInsurance("Unimed")
                .healthInsuranceNumber("1234567890")
                .healthInsuranceValidity(LocalDate.of(2025, 12, 31))
                .zipCode("12345-678")
                .city("São Paulo")
                .state("SP")
                .street("Rua Exemplo")
                .number("123")
                .complement("Apto 101")
                .neighborhood("Centro")
                .referencePoint("Próximo ao mercado")
                .password("password")
                .build();
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

    @Test
    public void testFindByName() {
        patientRepository.save(patient);
        List<Patient> patients = patientRepository.findByName("John Doe");
        assertThat(patients).isNotEmpty();
        assertThat(patients.get(0).getFullName()).isEqualTo("John Doe");
    }

    @Test
    public void testFindByPhone() {
        patientRepository.save(patient);
        List<Patient> patients = patientRepository.findByPhone("99999999999");
        assertThat(patients).isNotEmpty();
        assertThat(patients.get(0).getPhone()).isEqualTo("99999999999");
    }

    @Test
    public void testFindByCpf() {
        patient.setCpf(patient.cleanString(patient.getCpf()));
        patientRepository.save(patient);
        Patient foundPatient = patientRepository.findByCpf("12345678900");
        assertThat(foundPatient).isNotNull();
        assertThat(foundPatient.getCpf()).isEqualTo("12345678900");
    }
}