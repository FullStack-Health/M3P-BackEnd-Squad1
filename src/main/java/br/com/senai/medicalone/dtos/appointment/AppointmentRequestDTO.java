package br.com.senai.medicalone.dtos.appointment;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "DTO para criação e atualização de consultas")
public class AppointmentRequestDTO {

    @NotBlank
    @Size(min = 8, max = 64)
    @Schema(description = "Motivo da consulta", example = "Dor de cabeça")
    private String appointmentReason;

    @NotNull
    @Schema(description = "Data da consulta", example = "2023-10-01")
    private LocalDate appointmentDate;

    @NotNull
    @Schema(description = "Hora da consulta", example = "10:30")
    private LocalTime appointmentTime;

    @NotBlank
    @Size(min = 16, max = 1024)
    @Schema(description = "Descrição do problema", example = "Paciente apresenta dor intensa na cabeça.")
    private String problemDescription;

    @Schema(description = "Medicação prescrita", example = "Paracetamol")
    private String prescribedMedication;

    @Size(min = 16, max = 256)
    @Schema(description = "Observações", example = "Paciente deve retornar em uma semana.")
    private String observations;

    @NotNull
    @Schema(description = "ID do paciente associado ao agendamento", example = "1")
    private Long patientId;
}