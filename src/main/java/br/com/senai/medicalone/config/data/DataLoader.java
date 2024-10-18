package br.com.senai.medicalone.config.data;

import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        if (!userRepository.findByEmail("admin@example.com").isPresent()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@example.com");
            admin.setPhone("00000000000");
            admin.setBirthDate(LocalDate.of(1970, 1, 1));
            admin.setCpf("00000000000");
            admin.setPassword(passwordEncoder.encode("admin12345"));
            admin.setRole(RoleType.ADMIN);
            userRepository.save(admin);
        }

        if (!userRepository.findByEmail("medico@example.com").isPresent()) {
            User medico = new User();
            medico.setName("Medico");
            medico.setEmail("medico@example.com");
            medico.setPhone("11111111111");
            medico.setBirthDate(LocalDate.of(1980, 1, 1));
            medico.setCpf("11111111111");
            medico.setPassword(passwordEncoder.encode("medico12345"));
            medico.setRole(RoleType.MEDICO);
            userRepository.save(medico);
        }
    }
}