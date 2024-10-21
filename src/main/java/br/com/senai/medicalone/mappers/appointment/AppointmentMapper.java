package br.com.senai.medicalone.mappers.appointment;

import br.com.senai.medicalone.dtos.appointment.AppointmentRequestDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.entities.appointment.Appointment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Appointment toEntity(AppointmentRequestDTO dto) {
        return modelMapper.map(dto, Appointment.class);
    }

    public AppointmentResponseDTO toResponseDTO(Appointment entity) {
        return modelMapper.map(entity, AppointmentResponseDTO.class);
    }
}