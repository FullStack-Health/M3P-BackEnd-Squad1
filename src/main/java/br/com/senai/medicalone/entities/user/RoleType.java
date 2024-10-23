package br.com.senai.medicalone.entities.user;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {
    MEDICO,
    ADMIN,
    PACIENTE;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}