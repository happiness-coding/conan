package com.tech.easy.service;

import com.tech.easy.domain.model.UserEntity;
import com.tech.easy.domain.repository.UserRepository;
import com.tech.easy.exception.UserNotFoundException;
import com.tech.easy.web.dto.CreateUserRequest;
import com.tech.easy.web.dto.UserResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldReturnUserResponse() {
        CreateUserRequest req = new CreateUserRequest("john", "john@example.com");
        UserEntity entity = new UserEntity("john", "john@example.com");
        entity.setId(1L);

        when(userRepository.save(any(UserEntity.class))).thenReturn(entity);

        UserResponse response = userService.createUser(req);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("john");
    }

    @Test
    void getUser_whenFound_shouldReturnUserResponse() {
        UserEntity entity = new UserEntity("alice", "alice@example.com");
        entity.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(entity));

        UserResponse result = userService.getUser(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getUsername()).isEqualTo("alice");
    }

    @Test
    void getUser_whenNotFound_shouldThrowUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found, id=99");
    }
}
