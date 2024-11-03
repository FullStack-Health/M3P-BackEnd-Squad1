package br.com.senai.medicalone.services.patient;

import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.dtos.user.UserRequestDTO;
import br.com.senai.medicalone.dtos.user.UserResponseDTO;
import br.com.senai.medicalone.entities.patient.Patient;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.exceptions.customexceptions.PatientAlreadyExistsException;
import br.com.senai.medicalone.exceptions.customexceptions.PatientNotFoundException;
import br.com.senai.medicalone.mappers.patient.PatientMapper;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.repositories.user.UserRepository;
import br.com.senai.medicalone.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @InjectMocks
    private PatientService patientService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPatient_Success() {
        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setFullName("John Doe");
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setCpf("123.456.789-00");
        requestDTO.setGender("Masculino");
        requestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        requestDTO.setRg("1234567890");
        requestDTO.setRgIssuer("SSP");
        requestDTO.setMaritalStatus("Solteiro");
        requestDTO.setPhone("99999999999");
        requestDTO.setPlaceOfBirth("São Paulo");
        requestDTO.setEmergencyContact("99999999999");
        requestDTO.setZipCode("12345-678");
        requestDTO.setCity("São Paulo");
        requestDTO.setState("SP");
        requestDTO.setStreet("Rua Exemplo");
        requestDTO.setNumber("123");
        requestDTO.setComplement("Apto 101");
        requestDTO.setNeighborhood("Centro");
        requestDTO.setReferencePoint("Próximo ao mercado");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFullName("John Doe");
        patient.setEmail("john.doe@example.com");
        patient.setCpf("123.456.789-00");
        patient.setGender("Masculino");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setRg("1234567890");
        patient.setRgIssuer("SSP");
        patient.setMaritalStatus("Solteiro");
        patient.setPhone("99999999999");
        patient.setPlaceOfBirth("São Paulo");
        patient.setEmergencyContact("99999999999");
        patient.setZipCode("12345-678");
        patient.setCity("São Paulo");
        patient.setState("SP");
        patient.setStreet("Rua Exemplo");
        patient.setNumber("123");
        patient.setComplement("Apto 101");
        patient.setNeighborhood("Centro");
        patient.setReferencePoint("Próximo ao mercado");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);

        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientRepository.existsByCpf(anyString())).thenReturn(false);
        when(patientMapper.toEntity(any(PatientRequestDTO.class))).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(new PatientResponseDTO());
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        PatientResponseDTO responseDTO = patientService.createPatient(requestDTO);

        assertNotNull(responseDTO);
        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Test
    void createPatient_AlreadyExists() {
        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setFullName("John Doe");
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setCpf("123.456.789-00");
        requestDTO.setGender("Masculino");
        requestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        requestDTO.setRg("1234567890");
        requestDTO.setRgIssuer("SSP");
        requestDTO.setMaritalStatus("Solteiro");
        requestDTO.setPhone("99999999999");
        requestDTO.setPlaceOfBirth("São Paulo");
        requestDTO.setEmergencyContact("99999999999");
        requestDTO.setZipCode("12345-678");
        requestDTO.setCity("São Paulo");
        requestDTO.setState("SP");
        requestDTO.setStreet("Rua Exemplo");
        requestDTO.setNumber("123");
        requestDTO.setComplement("Apto 101");
        requestDTO.setNeighborhood("Centro");
        requestDTO.setReferencePoint("Próximo ao mercado");

        when(patientRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(PatientAlreadyExistsException.class, () -> patientService.createPatient(requestDTO));
    }

    @Test
    void getPatientById_Success() {
        Long id = 1L;
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFullName("John Doe");

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(new PatientResponseDTO());

        PatientResponseDTO responseDTO = patientService.getPatientById(id);

        assertNotNull(responseDTO);
    }

    @Test
    void getPatientById_NotFound() {
        Long id = 1L;

        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(id));
    }

    @Test
    void updatePatient_Success() {
        Long id = 1L;
        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setFullName("John Doe atualização");
        requestDTO.setGender("Masculino");
        requestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        requestDTO.setCpf("123.456.789-00");
        requestDTO.setRg("1234567890");
        requestDTO.setRgIssuer("SSP");
        requestDTO.setMaritalStatus("Casado");
        requestDTO.setPhone("(99) 9 9999-9999");
        requestDTO.setEmail("johnupdated@example.com");
        requestDTO.setPlaceOfBirth("São Paulo");
        requestDTO.setEmergencyContact("(99) 9 9999-9999");
        requestDTO.setAllergies(List.of("Poeira"));
        requestDTO.setSpecificCare(List.of("Acompanhamento cardiológico"));
        requestDTO.setHealthInsurance("Amil");
        requestDTO.setHealthInsuranceNumber("9876543210");
        requestDTO.setHealthInsuranceValidity(LocalDate.of(2026, 12, 31));
        requestDTO.setZipCode("12345-678");
        requestDTO.setCity("São Paulo");
        requestDTO.setState("SP");
        requestDTO.setStreet("Rua Exemplo");
        requestDTO.setNumber("456");
        requestDTO.setComplement("Casa");
        requestDTO.setNeighborhood("Centro");
        requestDTO.setReferencePoint("Perto da farmácia");

        Patient existingPatient = new Patient();
        existingPatient.setId(id);
        existingPatient.setFullName("John Doe");

        Patient updatedPatient = new Patient();
        updatedPatient.setId(id);
        updatedPatient.setFullName("John Doe atualização");

        when(patientRepository.findById(id)).thenReturn(Optional.of(existingPatient));
        when(patientMapper.toEntity(any(PatientRequestDTO.class))).thenReturn(updatedPatient);
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(new PatientResponseDTO());

        PatientResponseDTO responseDTO = patientService.updatePatient(id, requestDTO);

        assertNotNull(responseDTO);
    }

    @Test
    void updatePatient_NotFound() {
        Long id = 1L;
        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setFullName("John Doe Updated");
        requestDTO.setGender("Masculino");
        requestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        requestDTO.setCpf("123.456.789-00");
        requestDTO.setRg("1234567890");
        requestDTO.setRgIssuer("SSP");
        requestDTO.setMaritalStatus("Casado");
        requestDTO.setPhone("(99) 9 9999-9999");
        requestDTO.setEmail("johnupdated@example.com");
        requestDTO.setPlaceOfBirth("São Paulo");
        requestDTO.setEmergencyContact("(99) 9 9999-9999");
        requestDTO.setAllergies(List.of("Poeira"));
        requestDTO.setSpecificCare(List.of("Acompanhamento cardiológico"));
        requestDTO.setHealthInsurance("Amil");
        requestDTO.setHealthInsuranceNumber("9876543210");
        requestDTO.setHealthInsuranceValidity(LocalDate.of(2026, 12, 31));
        requestDTO.setZipCode("12345-678");
        requestDTO.setCity("São Paulo");
        requestDTO.setState("SP");
        requestDTO.setStreet("Rua Exemplo");
        requestDTO.setNumber("456");
        requestDTO.setComplement("Casa");
        requestDTO.setNeighborhood("Centro");
        requestDTO.setReferencePoint("Perto da farmácia");

        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> patientService.updatePatient(id, requestDTO));
    }

    @Test
    void deletePatient_Success() {
        Long id = 1L;
        Patient patient = new Patient();
        patient.setId(id);
        User user = new User();
        patient.setUser(user);

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        boolean result = patientService.deletePatient(id);

        assertTrue(result);
        verify(patientRepository, times(1)).deleteById(id);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deletePatient_NotFound() {
        Long id = 1L;

        lenient().when(patientRepository.existsById(id)).thenReturn(false);

        assertThrows(PatientNotFoundException.class, () -> patientService.deletePatient(id));
    }

    @Test
    void getAllPatients_Success() {
        Pageable pageable = mock(Pageable.class);
        Page<Patient> patientsPage = mock(Page.class);

        when(patientRepository.findAll(pageable)).thenReturn(patientsPage);
        when(patientsPage.map(any())).thenReturn(mock(Page.class));

        Page<PatientResponseDTO> responseDTOPage = patientService.getAllPatients(pageable);

        assertNotNull(responseDTOPage);
    }

    @Test
    void createPatient_MissingFullName_ShouldThrowException() {
        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setFullName("");

        assertThrows(BadRequestException.class, () -> patientService.createPatient(requestDTO));
    }

    @Test
    void getPatientByCpf_Success() {
        String cpf = "123.456.789-00";
        Patient patient = new Patient();
        patient.setCpf(cpf);
        patient.setFullName("John Doe");

        when(patientRepository.findByCpf(cpf)).thenReturn(patient);
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(new PatientResponseDTO());

        PatientResponseDTO responseDTO = patientService.getPatientByCpf(cpf);

        assertNotNull(responseDTO);
    }

    @Test
    void getPatientByCpf_NotFound() {
        String cpf = "123.456.789-00";

        when(patientRepository.findByCpf(cpf)).thenReturn(null);

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientByCpf(cpf));
    }

    @Test
    void getPatientsByName_Success() {
        String name = "John Doe";
        Patient patient = new Patient();
        patient.setFullName(name);

        when(patientRepository.findByName(name)).thenReturn(List.of(patient));
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(new PatientResponseDTO());

        List<PatientResponseDTO> responseDTOs = patientService.getPatientsByName(name);

        assertNotNull(responseDTOs);
        assertFalse(responseDTOs.isEmpty());
    }

    @Test
    void getPatientsByName_NotFound() {
        String name = "John Doe";

        when(patientRepository.findByName(name)).thenReturn(List.of());

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientsByName(name));
    }

    @Test
    void getPatientsByPhone_Success() {
        String phone = "99999999999";
        Patient patient = new Patient();
        patient.setPhone(phone);

        when(patientRepository.findByPhone(phone)).thenReturn(List.of(patient));
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(new PatientResponseDTO());

        List<PatientResponseDTO> responseDTOs = patientService.getPatientsByPhone(phone);

        assertNotNull(responseDTOs);
        assertFalse(responseDTOs.isEmpty());
    }

    @Test
    void getPatientsByPhone_NotFound() {
        String phone = "99999999999";

        when(patientRepository.findByPhone(phone)).thenReturn(List.of());

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientsByPhone(phone));
    }

    @Test
    void createPatient_MissingEmail_ShouldThrowException() {
        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setFullName("John Doe");

        assertThrows(BadRequestException.class, () -> patientService.createPatient(requestDTO));
    }

    @Test
    void updatePatient_MissingPhone_ShouldThrowException() {
        Long id = 1L;
        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setFullName("John Doe Updated");

        assertThrows(BadRequestException.class, () -> patientService.updatePatient(id, requestDTO));
    }

    @Test
    void createPatient_EmailAlreadyExists_ShouldThrowException() {
        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setFullName("John Doe");
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setGender("Masculino");
        requestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        requestDTO.setCpf("123.456.789-00");
        requestDTO.setRg("1234567890");
        requestDTO.setRgIssuer("SSP");
        requestDTO.setMaritalStatus("Solteiro");
        requestDTO.setPhone("99999999999");
        requestDTO.setPlaceOfBirth("São Paulo");
        requestDTO.setEmergencyContact("99999999999");
        requestDTO.setZipCode("12345-678");
        requestDTO.setCity("São Paulo");
        requestDTO.setState("SP");
        requestDTO.setStreet("Rua Exemplo");
        requestDTO.setNumber("123");
        requestDTO.setComplement("Apto 101");
        requestDTO.setNeighborhood("Centro");
        requestDTO.setReferencePoint("Próximo ao mercado");

        when(patientRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(PatientAlreadyExistsException.class, () -> patientService.createPatient(requestDTO));
    }

    @Test
    void createPatient_CpfAlreadyExists_ShouldThrowException() {
        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setFullName("John Doe");
        requestDTO.setCpf("123.456.789-00");
        requestDTO.setGender("Masculino");
        requestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        requestDTO.setRg("1234567890");
        requestDTO.setRgIssuer("SSP");
        requestDTO.setMaritalStatus("Solteiro");
        requestDTO.setPhone("99999999999");
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setPlaceOfBirth("São Paulo");
        requestDTO.setEmergencyContact("99999999999");
        requestDTO.setZipCode("12345-678");
        requestDTO.setCity("São Paulo");
        requestDTO.setState("SP");
        requestDTO.setStreet("Rua Exemplo");
        requestDTO.setNumber("123");
        requestDTO.setComplement("Apto 101");
        requestDTO.setNeighborhood("Centro");
        requestDTO.setReferencePoint("Próximo ao mercado");

        when(patientRepository.existsByCpf(anyString())).thenReturn(true);

        assertThrows(PatientAlreadyExistsException.class, () -> patientService.createPatient(requestDTO));
    }

    @Test
    void getPatientById_InvalidId_ShouldThrowException() {
        Long id = -1L;

        assertThrows(PatientNotFoundException.class, () -> patientService.getPatientById(id));
    }
}