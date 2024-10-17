package br.com.senai.medicalone.config;

import br.com.senai.medicalone.entities.RoleType;
import br.com.senai.medicalone.entities.User;
import br.com.senai.medicalone.repositories.UserRepository;
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
    }
}