package com.tech.easy.service;

import com.tech.easy.domain.model.UserEntity;
import com.tech.easy.domain.repository.UserRepository;
import com.tech.easy.exception.UserNotFoundException;
import com.tech.easy.web.dto.CreateUserRequest;
import com.tech.easy.web.dto.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_shouldPersistToDatabase() {
        // Given
        CreateUserRequest request = new CreateUserRequest("testuser", "testuser@example.com");

        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("testuser@example.com");

        // Verify data was actually persisted
        UserEntity savedEntity = userRepository.findById(response.getId()).orElseThrow();
        assertThat(savedEntity.getUsername()).isEqualTo("testuser");
        assertThat(savedEntity.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    void getUser_shouldRetrieveFromDatabase() {
        // Given - create a user directly via repository
        UserEntity entity = new UserEntity("integration", "integration@example.com");
        entity = userRepository.save(entity);
        Long savedId = entity.getId();

        // When - retrieve via service
        UserResponse response = userService.getUser(savedId);

        // Then - verify correct data is returned
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedId);
        assertThat(response.getUsername()).isEqualTo("integration");
        assertThat(response.getEmail()).isEqualTo("integration@example.com");
    }

    @Test
    void getUser_whenUserDoesNotExist_shouldThrowException() {
        // Given - a non-existent ID
        Long nonExistentId = 9999L;

        // When/Then - verify exception
        assertThatThrownBy(() -> userService.getUser(nonExistentId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(String.valueOf(nonExistentId));
    }

    @Test
    void userLifecycle_createAndRetrieve_shouldWorkTogether() {
        // Given - prepare creation request
        CreateUserRequest request = new CreateUserRequest("lifecycle", "lifecycle@example.com");

        // When - create user
        UserResponse createdUser = userService.createUser(request);

        // Then - retrieve and verify
        UserResponse retrievedUser = userService.getUser(createdUser.getId());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isEqualTo(createdUser.getId());
        assertThat(retrievedUser.getUsername()).isEqualTo("lifecycle");
        assertThat(retrievedUser.getEmail()).isEqualTo("lifecycle@example.com");
    }
}