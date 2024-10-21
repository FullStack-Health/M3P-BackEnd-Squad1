package br.com.senai.medicalone.services.appointment;

import br.com.senai.medicalone.dtos.appointment.AppointmentRequestDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.entities.appointment.Appointment;
import br.com.senai.medicalone.exceptions.customexceptions.AppointmentNotFoundException;
import br.com.senai.medicalone.mappers.appointment.AppointmentMapper;
import br.com.senai.medicalone.repositories.appointment.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper appointmentMapper; // Crie um mapper semelhante aos outros

    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto) {
        Appointment appointment = appointmentMapper.toEntity(dto);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(appointment);
    }

    public AppointmentResponseDTO getAppointmentById(Long id) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
        if (appointmentOptional.isEmpty()) {
            throw new AppointmentNotFoundException("Consulta não encontrada");
        }
        return appointmentMapper.toResponseDTO(appointmentOptional.get());
    }

    @Transactional
    public AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO dto) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
        if (appointmentOptional.isEmpty()) {
            throw new AppointmentNotFoundException("Consulta não encontrada");
        }
        Appointment appointment = appointmentOptional.get();
        appointment.setAppointmentReason(dto.getAppointmentReason());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointmentTime(dto.getAppointmentTime());
        appointment.setProblemDescription(dto.getProblemDescription());
        appointment.setPrescribedMedication(dto.getPrescribedMedication());
        appointment.setObservations(dto.getObservations());
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(appointment);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new AppointmentNotFoundException("Consulta não encontrada");
        }
        appointmentRepository.deleteById(id);
    }

    public List<AppointmentResponseDTO> listAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(appointmentMapper::toResponseDTO)
                .toList();
    }
}