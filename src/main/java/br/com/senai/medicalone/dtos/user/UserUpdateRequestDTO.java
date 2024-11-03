package br.com.senai.medicalone.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO para atualização de dados do usuário")
public class UserUpdateRequestDTO {
    @Schema(description = "Nome do usuário", example = "John Doe Updated")
    private String name;

    @Schema(description = "Email do usuário", example = "usuario26@example.com")
    private String email;

    @Schema(description = "Data de nascimento do usuário", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "Telefone do usuário", example = "1234567890")
    private String phone;

    @Schema(description = "CPF do usuário", example = "12345678900")
    private String cpf;
}