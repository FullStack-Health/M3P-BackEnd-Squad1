package br.com.senai.medicalone.config.doc;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi usuariosApi() {
        return GroupedOpenApi.builder()
                .group("usuarios")
                .pathsToMatch("/api/usuarios/**")
                .build();
    }

    @Bean
    public GroupedOpenApi pacientesApi() {
        return GroupedOpenApi.builder()
                .group("pacientes")
                .pathsToMatch("/api/pacientes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi consultasApi() {
        return GroupedOpenApi.builder()
                .group("consultas")
                .pathsToMatch("/api/consultas/**")
                .build();
    }

    @Bean
    public GroupedOpenApi examesApi() {
        return GroupedOpenApi.builder()
                .group("exames")
                .pathsToMatch("/api/exames/**")
                .build();
    }

    @Bean
    public GroupedOpenApi dashboardApi() {
        return GroupedOpenApi.builder()
                .group("dashboard")
                .pathsToMatch("/api/dashboard/**")
                .build();
    }
}