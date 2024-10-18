package br.com.senai.medicalone.entities.patient;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_pacients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidade que representa um paciente")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do paciente", example = "1")
    private Long id;

    @NotBlank
    @Size(min = 8, max = 64)
    @Column(nullable = false, length = 64)
    @Schema(description = "Nome completo do paciente", example = "John Doe")
    private String fullName;

    @NotBlank
    @Column(nullable = false)
    @Schema(description = "Gênero do paciente", example = "Masculino")
    private String gender;

    @NotNull
    @Past
    @Column(nullable = false)
    @Schema(description = "Data de nascimento do paciente", example = "1990-01-01")
    private LocalDate birthDate;

    @NotBlank
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")
    @Column(nullable = false, unique = true, length = 14)
    @Schema(description = "CPF do paciente", example = "123.456.789-00")
    private String cpf;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    @Schema(description = "RG do paciente com órgão expedidor", example = "1234567890 SSP")
    private String rg;

    @NotBlank
    @Column(nullable = false)
    @Schema(description = "Estado civil do paciente", example = "Solteiro")
    private String maritalStatus;

    @NotBlank
    @Pattern(regexp = "\\(\\d{2}\\) \\d \\d{4}-\\d{4}")
    @Column(nullable = false, length = 20)
    @Schema(description = "Telefone do paciente", example = "(99) 9 9999-9999")
    private String phone;

    @Email
    @Size(max = 255)
    @Column(length = 255)
    @Schema(description = "Email do paciente", example = "user@example.com")
    private String email;

    @NotBlank
    @Size(min = 8, max = 64)
    @Column(nullable = false, length = 64)
    @Schema(description = "Naturalidade do paciente", example = "São Paulo")
    private String placeOfBirth;

    @NotBlank
    @Pattern(regexp = "\\(\\d{2}\\) \\d \\d{4}-\\d{4}")
    @Column(nullable = false, length = 20)
    @Schema(description = "Contato de emergência do paciente", example = "(99) 9 9999-9999")
    private String emergencyContact;

    @ElementCollection
    @Schema(description = "Lista de alergias do paciente")
    private List<String> allergies;

    @ElementCollection
    @Schema(description = "Lista de cuidados específicos do paciente")
    private List<String> specificCare;

    @Schema(description = "Convênio do paciente", example = "Unimed")
    private String healthInsurance;

    @Schema(description = "Número do convênio do paciente", example = "1234567890")
    private String healthInsuranceNumber;

    @Schema(description = "Validade do convênio do paciente", example = "2025-12-31")
    private LocalDate healthInsuranceValidity;

    @Embedded
    @Schema(description = "Endereço do paciente")
    private Address address;


}