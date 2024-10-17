package br.com.senai.medicalone.dtos.users;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    private String newPassword;
}
