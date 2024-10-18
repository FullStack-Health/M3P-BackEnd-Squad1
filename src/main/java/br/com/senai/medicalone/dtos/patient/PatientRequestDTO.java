package br.com.senai.medicalone.dtos.patient;

import br.com.senai.medicalone.entities.patient.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "DTO para requisição de criação/atualização de paciente")
public class PatientRequestDTO {
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

    @Schema(description = "Endereço do paciente")
    private Address address;
}