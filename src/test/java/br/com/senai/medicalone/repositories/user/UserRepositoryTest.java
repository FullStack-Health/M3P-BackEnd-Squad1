package br.com.senai.medicalone.repositories.user;

import br.com.senai.medicalone.entities.user.RoleType;
import br.com.senai.medicalone.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setPhone("1234567890");
        user.setCpf("123.456.789-00");
        user.setPassword("password123");
        user.setRole(RoleType.ADMIN);
    }

    @Test
    public void testSaveUser() {
        User savedUser = userRepository.save(user);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void testFindByEmail() {
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testExistsByEmail() {
        userRepository.save(user);
        boolean exists = userRepository.existsByEmail("john.doe@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    public void testExistsByCpf() {
        user.setCpf(user.cleanString(user.getCpf()));
        userRepository.save(user);
        boolean exists = userRepository.existsByCpf("12345678900");
        assertThat(exists).isTrue();
    }

    @Test
    public void testDeleteUser() {
        User savedUser = userRepository.save(user);
        userRepository.deleteById(savedUser.getId());
        boolean exists = userRepository.existsById(savedUser.getId());
        assertThat(exists).isFalse();
    }
}