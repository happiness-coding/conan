package com.tech.easy.service;

import com.tech.easy.domain.model.UserEntity;
import com.tech.easy.domain.repository.UserRepository;
import com.tech.easy.exception.UserNotFoundException;
import com.tech.easy.web.dto.CreateUserRequest;
import com.tech.easy.web.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse createUser(CreateUserRequest request) {
        // Could do more advanced validation here or via a separate validator
        UserEntity entity = new UserEntity(request.getUsername(), request.getEmail());
        UserEntity saved = userRepository.save(entity);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail());
    }

    public UserResponse getUser(Long id) {
        Optional<UserEntity> entityOpt = userRepository.findById(id);
        if (entityOpt.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        UserEntity entity = entityOpt.get();
        return new UserResponse(entity.getId(), entity.getUsername(), entity.getEmail());
    }

    public UserResponse findById(Long id) {
        return null;
    }
}
