package br.com.senai.medicalone.services.appointment;

import br.com.senai.medicalone.dtos.appointment.AppointmentRequestDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.entities.appointment.Appointment;
import br.com.senai.medicalone.exceptions.customexceptions.AppointmentNotFoundException;
import br.com.senai.medicalone.mappers.appointment.AppointmentMapper;
import br.com.senai.medicalone.repositories.appointment.AppointmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private AppointmentMapper appointmentMapper;

    @Operation(summary = "Create a new appointment", description = "Método para criar uma nova consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso")
    })
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto) {
        Appointment appointment = appointmentMapper.toEntity(dto);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(appointment);
    }

    @Operation(summary = "Get appointment by ID", description = "Método para obter uma consulta pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
        if (appointmentOptional.isEmpty()) {
            throw new AppointmentNotFoundException("Consulta não encontrada");
        }
        return appointmentMapper.toResponseDTO(appointmentOptional.get());
    }

    @Operation(summary = "Update appointment", description = "Método para atualizar uma consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
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

    @Operation(summary = "Delete appointment", description = "Método para deletar uma consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Consulta deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new AppointmentNotFoundException("Consulta não encontrada");
        }
        appointmentRepository.deleteById(id);
    }

    @Operation(summary = "List appointments", description = "Método para listar consultas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultas listadas com sucesso")
    })
    public List<AppointmentResponseDTO> listAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(appointmentMapper::toResponseDTO)
                .toList();
    }
}