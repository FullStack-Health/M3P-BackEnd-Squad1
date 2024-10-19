package br.com.senai.medicalone.entities.patient;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Endereço do paciente")
public class Address {

    @NotBlank
    @Pattern(regexp = "\\d{5}-\\d{3}")
    @Schema(description = "CEP do endereço", example = "12345-678")
    private String zipCode;

    @NotBlank
    @Schema(description = "Cidade do endereço", example = "São Paulo")
    private String city;

    @NotBlank
    @Schema(description = "Estado do endereço", example = "SP")
    private String state;

    @NotBlank
    @Schema(description = "Logradouro do endereço", example = "Rua Exemplo")
    private String street;

    @NotBlank
    @Schema(description = "Número do endereço", example = "123")
    private String number;

    @Schema(description = "Complemento do endereço", example = "Apto 101")
    private String complement;

    @NotBlank
    @Schema(description = "Bairro do endereço", example = "Centro")
    private String neighborhood;

    @Schema(description = "Ponto de referência do endereço", example = "Próximo ao mercado")
    private String referencePoint;
}