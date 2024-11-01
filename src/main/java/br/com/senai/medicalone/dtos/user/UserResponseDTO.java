package br.com.senai.medicalone.dtos.user;

import br.com.senai.medicalone.entities.user.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "DTO para resposta de dados do usuário")
public class UserResponseDTO {
    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "John Doe")
    private String name;

    @Schema(description = "Email do usuário", example = "user@example.com")
    private String email;

    @Schema(description = "Data de nascimento do usuário", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "Telefone do usuário", example = "(99) 9 9999-9999")
    private String phone;

    @Schema(description = "CPF do usuário", example = "123.456.789-00")
    private String cpf;

    @Schema(description = "Papel do usuário", example = "ADMIN")
    private RoleType role;

    @Schema(description = "ID do paciente associado", example = "1")
    private Long patientId;

    @Schema(description = "Senha mascarada do usuário", example = "pass****")
    private String maskedPassword;
}