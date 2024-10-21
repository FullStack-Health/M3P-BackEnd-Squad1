package br.com.senai.medicalone.services.patient;

import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.entities.patient.Patient;
import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.exceptions.customexceptions.PatientAlreadyExistsException;
import br.com.senai.medicalone.exceptions.customexceptions.PatientNotFoundException;
import br.com.senai.medicalone.mappers.patient.PatientMapper;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import br.com.senai.medicalone.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Operation(summary = "Create a new patient", description = "Método para criar um novo paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paciente criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        validatePatientRequestDTO(patientRequestDTO);

        if (patientRepository.existsByEmail(patientRequestDTO.getEmail()) || patientRepository.existsByCpf(patientRequestDTO.getCpf())) {
            throw new PatientAlreadyExistsException("Paciente já cadastrado");
        }

        try {
            Patient patient = patientMapper.toEntity(patientRequestDTO);
            patient.setPassword(patient.getCpf());
            patient = patientRepository.save(patient);

            User user = new User();
            user.setName(patient.getFullName());
            user.setEmail(patient.getEmail());
            user.setBirthDate(patient.getBirthDate());
            user.setPhone(patient.getPhone());
            user.setCpf(patient.getCpf());
            user.setPassword(patient.getCpf());
            user.setRole(RoleType.PACIENTE);
            userService.createUser(user);

            return patientMapper.toResponseDTO(patient);
        } catch (DataIntegrityViolationException ex) {
            throw new PatientAlreadyExistsException("Paciente já cadastrado ");
        }
    }

    @Operation(summary = "Get patient by ID", description = "Método para obter um paciente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public PatientResponseDTO getPatientById(Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            return patientMapper.toResponseDTO(patient.get());
        } else {
            throw new PatientNotFoundException("Paciente não encontrado com ID: " + id);
        }
    }

    @Operation(summary = "Update a patient", description = "Método para atualizar um paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    @Transactional
    public PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO) {
        validatePatientRequestDTO(patientRequestDTO);

        Optional<Patient> patientOptional = patientRepository.findById(id);
        if (patientOptional.isPresent()) {
            Patient existingPatient = patientOptional.get();
            Patient updatedPatient = patientMapper.toEntity(patientRequestDTO);
            updatedPatient.setId(id);
            updatedPatient.setPassword(existingPatient.getPassword());
            updatedPatient = patientRepository.save(updatedPatient);
            return patientMapper.toResponseDTO(updatedPatient);
        } else {
            throw new PatientNotFoundException("Paciente não encontrado com ID: " + id);
        }
    }

    @Operation(summary = "Delete a patient", description = "Método para excluir um paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paciente excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    @Transactional
    public boolean deletePatient(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        } else {
            throw new PatientNotFoundException("Paciente não encontrado com ID: " + id);
        }
    }

    @Operation(summary = "Get all patients", description = "Método para obter todos os pacientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pacientes encontrados com sucesso")
    })
    public Page<PatientResponseDTO> getAllPatients(Pageable pageable) {
        Page<Patient> patients = patientRepository.findAll(pageable);
        return patients.map(patientMapper::toResponseDTO);
    }

    private void validatePatientRequestDTO(PatientRequestDTO patientRequestDTO) {
        if (patientRequestDTO.getFullName() == null || patientRequestDTO.getFullName().isEmpty()) {
            throw new BadRequestException("dados ausentes: fullName");
        }
        if (patientRequestDTO.getGender() == null || patientRequestDTO.getGender().isEmpty()) {
            throw new BadRequestException("dados ausentes: gender");
        }
        if (patientRequestDTO.getBirthDate() == null) {
            throw new BadRequestException("dados ausentes: birthDate");
        }
        if (patientRequestDTO.getCpf() == null || patientRequestDTO.getCpf().isEmpty()) {
            throw new BadRequestException("dados ausentes: cpf");
        }
        if (patientRequestDTO.getRg() == null || patientRequestDTO.getRg().isEmpty()) {
            throw new BadRequestException("dados ausentes: rg");
        }
        if (patientRequestDTO.getRgIssuer() == null || patientRequestDTO.getRgIssuer().isEmpty()) {
            throw new BadRequestException("dados ausentes: rgIssuer");
        }
        if (patientRequestDTO.getMaritalStatus() == null || patientRequestDTO.getMaritalStatus().isEmpty()) {
            throw new BadRequestException("dados ausentes: maritalStatus");
        }
        if (patientRequestDTO.getPhone() == null || patientRequestDTO.getPhone().isEmpty()) {
            throw new BadRequestException("dados ausentes: phone");
        }
        if (patientRequestDTO.getPlaceOfBirth() == null || patientRequestDTO.getPlaceOfBirth().isEmpty()) {
            throw new BadRequestException("dados ausentes: placeOfBirth");
        }
        if (patientRequestDTO.getEmergencyContact() == null || patientRequestDTO.getEmergencyContact().isEmpty()) {
            throw new BadRequestException("dados ausentes: emergencyContact");
        }
        if (patientRequestDTO.getAddress() == null) {
            throw new BadRequestException("dados ausentes: address");
        }
        if (patientRequestDTO.getAddress().getZipCode() == null || patientRequestDTO.getAddress().getZipCode().isEmpty()) {
            throw new BadRequestException("dados ausentes: address.zipCode");
        }
        if (patientRequestDTO.getAddress().getCity() == null || patientRequestDTO.getAddress().getCity().isEmpty()) {
            throw new BadRequestException("dados ausentes: address.city");
        }
        if (patientRequestDTO.getAddress().getState() == null || patientRequestDTO.getAddress().getState().isEmpty()) {
            throw new BadRequestException("dados ausentes: address.state");
        }
        if (patientRequestDTO.getAddress().getStreet() == null || patientRequestDTO.getAddress().getStreet().isEmpty()) {
            throw new BadRequestException("dados ausentes: address.street");
        }
        if (patientRequestDTO.getAddress().getNumber() == null || patientRequestDTO.getAddress().getNumber().isEmpty()) {
            throw new BadRequestException("dados ausentes: address.number");
        }
        if (patientRequestDTO.getAddress().getNeighborhood() == null || patientRequestDTO.getAddress().getNeighborhood().isEmpty()) {
            throw new BadRequestException("dados ausentes: address.neighborhood");
        }
    }

    @Operation(summary = "Get patient by CPF", description = "Método para obter um paciente pelo CPF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public PatientResponseDTO getPatientByCpf(String cpf) {
        Patient patient = patientRepository.findByCpf(cpf);
        if (patient != null) {
            return patientMapper.toResponseDTO(patient);
        } else {
            throw new PatientNotFoundException("Paciente não encontrado com CPF: " + cpf);
        }
    }

    @Operation(summary = "Get patients by name", description = "Método para obter pacientes pelo nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pacientes encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pacientes não encontrados")
    })
    public List<PatientResponseDTO> getPatientsByName(String name) {
        List<Patient> patients = patientRepository.findByName(name);
        if (!patients.isEmpty()) {
            return patients.stream().map(patientMapper::toResponseDTO).collect(Collectors.toList());
        } else {
            throw new PatientNotFoundException("Pacientes não encontrados com o nome: " + name);
        }
    }

    @Operation(summary = "Get patients by phone", description = "Método para obter pacientes pelo telefone")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pacientes encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pacientes não encontrados")
    })
    public List<PatientResponseDTO> getPatientsByPhone(String phone) {
        List<Patient> patients = patientRepository.findByPhone(phone);
        if (!patients.isEmpty()) {
            return patients.stream().map(patientMapper::toResponseDTO).collect(Collectors.toList());
        } else {
            throw new PatientNotFoundException("Pacientes não encontrados com o telefone: " + phone);
        }
    }


}