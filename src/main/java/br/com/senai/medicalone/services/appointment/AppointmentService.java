package br.com.senai.medicalone.services.appointment;

import br.com.senai.medicalone.dtos.appointment.AppointmentRequestDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.entities.appointment.Appointment;
import br.com.senai.medicalone.exceptions.customexceptions.AppointmentNotFoundException;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.mappers.appointment.AppointmentMapper;
import br.com.senai.medicalone.repositories.appointment.AppointmentRepository;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private PatientRepository patientRepository;


    @Operation(summary = "Cria uma nova consulta", description = "Método para criar uma nova consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso")
    })
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto) {
        if (dto.getAppointmentReason() == null || dto.getAppointmentReason().isEmpty()) {
            throw new BadRequestException("Motivo da consulta é obrigatório");
        }
        if (dto.getAppointmentDate() == null) {
            throw new BadRequestException("Data da consulta é obrigatória");
        }
        if (dto.getAppointmentTime() == null) {
            throw new BadRequestException("Hora da consulta é obrigatória");
        }
        if (dto.getProblemDescription() == null || dto.getProblemDescription().isEmpty()) {
            throw new BadRequestException("Descrição do problema é obrigatória");
        }
        if (dto.getPatientId() == null || !patientRepository.existsById(dto.getPatientId())) {
            throw new BadRequestException("Paciente não encontrado");
        }

        Appointment appointment = appointmentMapper.toEntity(dto);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(appointment);
    }

    @Operation(summary = "Busca consulta por ID", description = "Método para obter uma consulta pelo ID")
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

    @Operation(summary = "Atualiza consulta", description = "Método para atualizar uma consulta")
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

    @Operation(summary = "Deleta uma consulta", description = "Método para deletar uma consulta")
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

    @Operation(summary = "Lista consultas", description = "Método para listar consultas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultas listadas com sucesso")
    })
    public Page<AppointmentResponseDTO> listAppointments(Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findAll(pageable);
        return appointments.map(appointmentMapper::toResponseDTO);
    }

    public List<AppointmentResponseDTO> getAppointmentsByPatientId(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments.stream().map(appointmentMapper::toResponseDTO).collect(Collectors.toList());
    }
}