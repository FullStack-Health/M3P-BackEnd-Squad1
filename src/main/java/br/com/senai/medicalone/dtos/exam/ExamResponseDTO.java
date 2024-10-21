package br.com.senai.medicalone.dtos.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "DTO para resposta de exames")
public class ExamResponseDTO {

    @Schema(description = "ID do exame", example = "1")
    private Long id;

    @Schema(description = "Nome do exame", example = "Hemograma Completo")
    private String name;

    @Schema(description = "Data do exame", example = "2023-10-01")
    private LocalDate examDate;

    @Schema(description = "Horário do exame", example = "08:30")
    private LocalTime examTime;

    @Schema(description = "Tipo do exame", example = "Sangue")
    private String type;

    @Schema(description = "Laboratório", example = "Laboratório XYZ")
    private String laboratory;

    @Schema(description = "URL do documento", example = "http://example.com/document.pdf")
    private String documentUrl;

    @Schema(description = "Resultados do exame", example = "Resultados detalhados do exame")
    private String results;

    @Schema(description = "ID do paciente associado ao exame", example = "1")
    private Long patientId;
}