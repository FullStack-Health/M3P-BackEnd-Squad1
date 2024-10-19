package br.com.senai.medicalone.dtos.user;

import br.com.senai.medicalone.entities.user.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "DTO para requisição de dados do usuário")
public class UserRequestDTO {
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

    @Schema(description = "Senha do usuário", example = "password123")
    private String password;

    @Schema(description = "Papel do usuário", example = "ADMIN")
    private RoleType role;
}