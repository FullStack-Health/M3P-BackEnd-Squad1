package br.com.senai.medicalone.dtos.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "DTO para respostas de consultas")
public class AppointmentResponseDTO {

    @Schema(description = "ID da consulta", example = "1")
    private Long id;

    @Schema(description = "Motivo da consulta", example = "Dor de cabeça")
    private String appointmentReason;

    @Schema(description = "Data da consulta", example = "2023-10-01")
    private LocalDate appointmentDate;

    @Schema(description = "Hora da consulta", example = "10:30")
    private LocalTime appointmentTime;

    @Schema(description = "Descrição do problema", example = "Paciente apresenta dor intensa na cabeça.")
    private String problemDescription;

    @Schema(description = "Medicação prescrita", example = "Paracetamol")
    private String prescribedMedication;

    @Schema(description = "Observações", example = "Paciente deve retornar em uma semana.")
    private String observations;

    @Schema(description = "ID do paciente associado ao agendamento", example = "1")
    private Long patientId;
}