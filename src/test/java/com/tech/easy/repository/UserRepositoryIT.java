package com.tech.easy.repository;

import com.tech.easy.domain.model.UserEntity;
import com.tech.easy.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindUser_shouldPersistData() {
        UserEntity user = new UserEntity("bob", "bob@example.com");
        userRepository.save(user);

        var saved = userRepository.findAll();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getUsername()).isEqualTo("bob");
    }

    @Test
    void findByUsername_shouldReturnMatchingUser() {
        userRepository.save(new UserEntity("carol", "carol@example.com"));
        var result = userRepository.findByUsername("carol");
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("carol@example.com");    }

}
