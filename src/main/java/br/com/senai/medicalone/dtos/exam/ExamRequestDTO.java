package br.com.senai.medicalone.dtos.exam;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "DTO para criação e atualização de exames")
public class ExamRequestDTO {

    @NotBlank
    @Size(min = 8, max = 64)
    @Schema(description = "Nome do exame", example = "Hemograma Completo")
    private String name;

    @NotNull
    @Schema(description = "Data do exame", example = "2023-10-01")
    private LocalDate examDate;

    @NotNull
    @Schema(description = "Horário do exame", example = "08:30")
    private LocalTime examTime;

    @NotBlank
    @Size(min = 4, max = 32)
    @Schema(description = "Tipo do exame", example = "Sangue")
    private String type;

    @NotBlank
    @Size(min = 4, max = 32)
    @Schema(description = "Laboratório", example = "Laboratório XYZ")
    private String laboratory;

    @Schema(description = "URL do documento", example = "http://example.com/document.pdf")
    private String documentUrl;

    @Size(min = 16, max = 1024)
    @Schema(description = "Resultados do exame", example = "Resultados detalhados do exame")
    private String results;

    @NotNull
    @Schema(description = "ID do paciente associado ao exame", example = "1")
    private Long patientId;
}