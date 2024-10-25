package br.com.senai.medicalone.entities.appointment;

import br.com.senai.medicalone.entities.patient.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tb_appointments")
@Data
@NoArgsConstructor
@Schema(description = "Entidade que representa uma consulta")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID da consulta", example = "1")
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 8, max = 64)
    @Column(nullable = false, length = 64)
    @Schema(description = "Motivo da consulta", example = "Dor de cabeça")
    private String appointmentReason;

    @NotNull
    @Column(nullable = false)
    @Schema(description = "Data da consulta", example = "2023-10-01")
    private LocalDate appointmentDate;

    @NotNull
    @Column(nullable = false)
    @Schema(description = "Hora da consulta", example = "10:30")
    private LocalTime appointmentTime;

    @NotBlank
    @Size(min = 16, max = 1024)
    @Column(nullable = false, length = 1024)
    @Schema(description = "Descrição do problema", example = "Paciente apresenta dor intensa na cabeça.")
    private String problemDescription;

    @Schema(description = "Medicação prescrita", example = "Paracetamol")
    private String prescribedMedication;

    @Size(min = 16, max = 256)
    @Schema(description = "Observações", example = "Paciente deve retornar em uma semana.")
    private String observations;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @Schema(description = "ID do paciente associado a consulta")
    private Patient patient;
}