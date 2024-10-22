package br.com.senai.medicalone.entities.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "tb_pre_register_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidade que representa um usuário pré-registrado", example = "{\"email\": \"usuario2@example.com\", \"password\": \"senha123\", \"role\": \"ADMIN\"}")
public class PreRegisterUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(nullable = false, unique = true, length = 255)
    @Schema(description = "Email do usuário", example = "user@example.com")
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    @Schema(description = "Senha do usuário", example = "password123")
    private String password;

    @NotNull
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