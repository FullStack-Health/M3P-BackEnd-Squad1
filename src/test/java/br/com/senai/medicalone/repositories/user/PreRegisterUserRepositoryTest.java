package br.com.senai.medicalone.repositories.user;

import br.com.senai.medicalone.entities.user.PreRegisterUser;
import br.com.senai.medicalone.entities.user.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PreRegisterUserRepositoryTest {

    @Autowired
    private PreRegisterUserRepository preRegisterUserRepository;

    private PreRegisterUser preRegisterUser;

    @BeforeEach
    public void setUp() {
        preRegisterUser = new PreRegisterUser();
        preRegisterUser.setEmail("preuser@example.com");
        preRegisterUser.setPassword("password123");
        preRegisterUser.setRole(RoleType.ADMIN);
    }

    @Test
    public void testSavePreRegisterUser() {
        PreRegisterUser savedUser = preRegisterUserRepository.save(preRegisterUser);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void testFindByEmail() {
        preRegisterUserRepository.save(preRegisterUser);
        Optional<PreRegisterUser> foundUser = preRegisterUserRepository.findByEmail("preuser@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("preuser@example.com");
    }

    @Test
    public void testExistsByEmail() {
        preRegisterUserRepository.save(preRegisterUser);
        boolean exists = preRegisterUserRepository.existsByEmail("preuser@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    public void testDeletePreRegisterUser() {
        PreRegisterUser savedUser = preRegisterUserRepository.save(preRegisterUser);
        preRegisterUserRepository.deleteById(savedUser.getId());
        boolean exists = preRegisterUserRepository.existsById(savedUser.getId());
        assertThat(exists).isFalse();
    }
}