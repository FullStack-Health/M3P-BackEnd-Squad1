package br.com.senai.medicalone.dtos.users;

import br.com.senai.medicalone.entities.RoleType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserRequestDTO {
    private String name;
    private String email;
    private LocalDate birthDate;
    private String phone;
    private String cpf;
    private String password;
    private RoleType role;
}