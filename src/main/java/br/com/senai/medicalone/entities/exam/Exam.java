package br.com.senai.medicalone.entities.exam;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import br.com.senai.medicalone.entities.patient.Patient;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tb_exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidade que representa um exame")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do exame", example = "1")
    private Long id;

    @NotBlank
    @Size(min = 8, max = 64)
    @Column(nullable = false, length = 64)
    @Schema(description = "Nome do exame", example = "Hemograma Completo")
    private String name;

    @NotNull
    @Column(nullable = false)
    @Schema(description = "Data do exame", example = "2023-10-01")
    private LocalDate examDate;

    @NotNull
    @Column(nullable = false)
    @Schema(description = "Horário do exame", example = "08:30")
    private LocalTime examTime;

    @NotBlank
    @Size(min = 4, max = 32)
    @Column(nullable = false, length = 32)
    @Schema(description = "Tipo do exame", example = "Sangue")
    private String type;

    @NotBlank
    @Size(min = 4, max = 32)
    @Column(nullable = false, length = 32)
    @Schema(description = "Laboratório", example = "Laboratório XYZ")
    private String laboratory;

    @Schema(description = "URL do documento", example = "http://example.com/document.pdf")
    private String documentUrl;

    @Size(min = 16, max = 1024)
    @Schema(description = "Resultados do exame", example = "Resultados detalhados do exame")
    private String results;

    @NotNull(message = "Paciente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @Schema(description = "Paciente associado ao exame")
    private Patient patient;
}