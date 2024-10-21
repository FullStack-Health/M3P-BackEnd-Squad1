package br.com.senai.medicalone.entities.patient;

import br.com.senai.medicalone.entities.exam.Exam;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_pacients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    @Schema(description = "Data de nascimento do paciente", example = "1990-01-01")
    private LocalDate birthDate;

    @NotBlank
    @Column(nullable = false, unique = true, length = 14)
    @Schema(description = "CPF do paciente", example = "123.456.789-00")
    private String cpf;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    @Schema(description = "RG do paciente", example = "1234567890")
    private String rg;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    @Schema(description = "Órgão expedidor do RG", example = "SSP")
    private String rgIssuer;

    @NotBlank
    @Column(nullable = false)
    @Schema(description = "Estado civil do paciente", example = "Solteiro")
    private String maritalStatus;

    @NotBlank
    @Column(nullable = false, length = 20)
    @Schema(description = "Telefone do paciente", example = "99999999999")
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
    @Column(nullable = false, length = 20)
    @Schema(description = "Contato de emergência do paciente", example = "99999999999")
    private String emergencyContact;

    @ElementCollection
    @CollectionTable(name = "patient_allergies", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "allergy")
    @Schema(description = "Lista de alergias do paciente")
    private List<String> allergies;

    @ElementCollection
    @CollectionTable(name = "patient_specific_care", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "specific_care")
    @Schema(description = "Lista de cuidados específicos do paciente")
    private List<String> specificCare;

    @Schema(description = "Convênio do paciente", example = "Unimed")
    private String healthInsurance;

    @Schema(description = "Número do convênio do paciente", example = "1234567890")
    private String healthInsuranceNumber;

    @Schema(description = "Validade do convênio do paciente", example = "2025-12-31")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate healthInsuranceValidity;

    @Embedded
    @Schema(description = "Endereço do paciente")
    private Address address;

    @NotBlank
    @Column(nullable = false)
    @Schema(description = "Senha do paciente")
    private String password;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Lista de exames do paciente")
    private List<Exam> exams = new ArrayList<>();

    @PrePersist
    @PreUpdate
    @Schema(description = "Método chamado antes de persistir ou atualizar a entidade")
    private void preProcess() {
        this.cpf = cleanString(this.cpf);
        this.phone = cleanString(this.phone);
        this.emergencyContact = cleanString(this.emergencyContact);
        this.healthInsuranceNumber = cleanString(this.healthInsuranceNumber);
        this.rg = cleanString(this.rg);
        if (this.address != null) {
            this.address.setZipCode(cleanString(this.address.getZipCode()));
        }
    }

    @Schema(description = "Método para limpar uma string, removendo todos os caracteres não numéricos")
    public String cleanString(String value) {
        return value != null ? value.replaceAll("\\D", "") : null;
    }
}