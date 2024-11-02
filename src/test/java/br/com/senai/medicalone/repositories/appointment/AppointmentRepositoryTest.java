package br.com.senai.medicalone.repositories.appointment;

import br.com.senai.medicalone.entities.appointment.Appointment;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Appointment appointment;
    private Patient patient;

    @BeforeEach
    public void setUp() {
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();

        patient = createAndSavePatient();

        appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setAppointmentTime(LocalTime.now());
        appointment.setAppointmentReason("Consulta de rotina");
        appointment.setProblemDescription("Paciente apresenta dor intensa na cabeça.");
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
    public void testFindByPatientId() {
        appointmentRepository.save(appointment);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> appointments = appointmentRepository.findByPatientId(patient.getId(), pageable);
        assertThat(appointments).isNotEmpty();
        assertThat(appointments.getContent().get(0).getPatient().getId()).isEqualTo(patient.getId());
    }

    @Test
    public void testFindAll() {
        appointmentRepository.save(appointment);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> appointments = appointmentRepository.findAll(pageable);
        assertThat(appointments).isNotEmpty();
    }

    @Test
    public void testFindByPatientIdAndAppointmentDateAndAppointmentTime() {
        appointmentRepository.save(appointment);
        Optional<Appointment> foundAppointment = appointmentRepository.findByPatientIdAndAppointmentDateAndAppointmentTime(
                patient.getId(), appointment.getAppointmentDate(), appointment.getAppointmentTime());
        assertThat(foundAppointment).isPresent();
        assertThat(foundAppointment.get().getPatient().getId()).isEqualTo(patient.getId());
    }

    @Test
    public void testSaveAppointment() {
        Appointment savedAppointment = appointmentRepository.save(appointment);
        assertThat(savedAppointment).isNotNull();
        assertThat(savedAppointment.getId()).isNotNull();
    }

    @Test
    public void testDeleteAppointment() {
        Appointment savedAppointment = appointmentRepository.save(appointment);
        appointmentRepository.deleteById(savedAppointment.getId());
        boolean exists = appointmentRepository.existsById(savedAppointment.getId());
        assertThat(exists).isFalse();
    }
}