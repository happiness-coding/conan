package com.tech.easy.web.controller;

import com.tech.easy.common.BaseResponse;
import com.tech.easy.common.Version;
import com.tech.easy.service.UserService;
import com.tech.easy.web.dto.CreateUserRequest;
import com.tech.easy.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<BaseResponse<UserResponse>> createUser(@RequestBody CreateUserRequest dto) {
        UserResponse created = userService.createUser(dto);

        return created201("User created successfully", created);
    }

    @GetMapping("/{id}")
    @Version(1)
    public ResponseEntity<BaseResponse<UserResponse>> getUser(@PathVariable Long id) {
        UserResponse user = userService.findById(id);
        return ok200("User fetched", user);
    }

}
