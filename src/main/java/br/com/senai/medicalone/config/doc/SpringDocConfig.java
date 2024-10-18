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
}