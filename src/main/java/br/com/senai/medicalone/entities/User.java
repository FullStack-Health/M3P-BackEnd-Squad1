package br.com.senai.medicalone.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "tb_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidade que representa um usuário")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Column(nullable = false, length = 255)
    @Schema(description = "Nome do usuário", example = "John Doe")
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    @Schema(description = "Email do usuário", example = "user@example.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Data de nascimento do usuário", example = "1990-01-01")
    private LocalDate birthDate;

    @Column(nullable = false, length = 20)
    @Schema(description = "Telefone do usuário", example = "1234567890")
    private String phone;

    @Column(nullable = false, unique = true, length = 14)
    @Schema(description = "CPF do usuário", example = "123.456.789-00")
    private String cpf;

    @Column(nullable = false, length = 255)
    @Schema(description = "Senha do usuário", example = "password123")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Role do usuário", example = "ADMIN")
    private RoleType role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}