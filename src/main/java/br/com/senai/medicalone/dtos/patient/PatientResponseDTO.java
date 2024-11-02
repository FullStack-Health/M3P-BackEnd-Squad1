package br.com.senai.medicalone.dtos.patient;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "DTO para resposta de dados do paciente")
public class PatientResponseDTO {

    @Schema(description = "ID do paciente", example = "1")
    private Long id;

    @Schema(description = "Nome completo do paciente", example = "John Doe")
    private String fullName;

    @Schema(description = "Gênero do paciente", example = "Masculino")
    private String gender;

    @Schema(description = "Data de nascimento do paciente", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "CPF do paciente", example = "123.456.789-00")
    private String cpf;

    @Schema(description = "RG do paciente", example = "1234567890")
    private String rg;

    @Schema(description = "Órgão expedidor do RG", example = "SSP")
    private String rgIssuer;

    @Schema(description = "Estado civil do paciente", example = "Solteiro")
    private String maritalStatus;

    @Schema(description = "Telefone do paciente", example = "(99) 9 9999-9999")
    private String phone;

    @Schema(description = "Email do paciente", example = "user@example.com")
    private String email;

    @Schema(description = "Naturalidade do paciente", example = "São Paulo")
    private String placeOfBirth;

    @Schema(description = "Contato de emergência do paciente", example = "(99) 9 9999-9999")
    private String emergencyContact;

    @Schema(description = "Lista de alergias do paciente")
    private List<String> allergies;

    @Schema(description = "Lista de cuidados específicos do paciente")
    private List<String> specificCare;

    @Schema(description = "Convênio do paciente", example = "Unimed")
    private String healthInsurance;

    @Schema(description = "Número do convênio do paciente", example = "1234567890")
    private String healthInsuranceNumber;

    @Schema(description = "Validade do convênio do paciente", example = "2025-12-31")
    private LocalDate healthInsuranceValidity;

    @Schema(description = "CEP do endereço", example = "12345-678")
    private String zipCode;

    @Schema(description = "Cidade do endereço", example = "São Paulo")
    private String city;

    @Schema(description = "Estado do endereço", example = "SP")
    private String state;

    @Schema(description = "Logradouro do endereço", example = "Rua Exemplo")
    private String street;

    @Schema(description = "Número do endereço", example = "123")
    private String number;

    @Schema(description = "Complemento do endereço", example = "Apto 101")
    private String complement;

    @Schema(description = "Bairro do endereço", example = "Centro")
    private String neighborhood;

    @Schema(description = "Ponto de referência do endereço", example = "Próximo ao mercado")
    private String referencePoint;
}