package br.com.senai.medicalone.mappers.patient;

import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.entities.patient.Patient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Patient toEntity(PatientRequestDTO dto) {
        return modelMapper.map(dto, Patient.class);
    }

    public PatientResponseDTO toResponseDTO(Patient entity) {
        return modelMapper.map(entity, PatientResponseDTO.class);
    }
}