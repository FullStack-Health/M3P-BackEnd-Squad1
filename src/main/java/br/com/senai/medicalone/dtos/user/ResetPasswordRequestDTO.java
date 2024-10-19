package br.com.senai.medicalone.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para requisição de redefinição de senha")
public class ResetPasswordRequestDTO {
    @Schema(description = "Nova senha do usuário", example = "newPassword123")
    private String newPassword;
}