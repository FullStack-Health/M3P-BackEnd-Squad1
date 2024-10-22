package br.com.senai.medicalone.dtos.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para requisição de login do usuário")
public class LoginRequestDTO {
    @Schema(description = "Email do usuário", example = "admin@example.com")
    private String email;

    @Schema(description = "Senha do usuário", example = "admin12345")
    private String password;
}