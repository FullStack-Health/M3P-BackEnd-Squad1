package br.com.senai.medicalone.services.patient;

import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.entities.patient.Patient;
import br.com.senai.medicalone.mappers.patient.PatientMapper;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        Patient patient = patientMapper.toEntity(patientRequestDTO);
        patient = patientRepository.save(patient);
        return patientMapper.toResponseDTO(patient);
    }

    public PatientResponseDTO getPatientById(Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            return patientMapper.toResponseDTO(patient.get());
        } else {
            throw new RuntimeException("Paciente não encontrado");
        }
    }

    @Transactional
    public PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO) {
        Optional<Patient> patientOptional = patientRepository.findById(id);
        if (patientOptional.isPresent()) {
            Patient patient = patientMapper.toEntity(patientRequestDTO);
            patient.setId(id);
            patient = patientRepository.save(patient);
            return patientMapper.toResponseDTO(patient);
        } else {
            throw new RuntimeException("Paciente não encontrado");
        }
    }

    @Transactional
    public void deletePatient(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
        } else {
            throw new RuntimeException("Paciente não encontrado");
        }
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(patientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}