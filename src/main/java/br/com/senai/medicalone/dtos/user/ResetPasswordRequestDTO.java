package br.com.senai.medicalone.dtos.user;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    private String newPassword;
}
