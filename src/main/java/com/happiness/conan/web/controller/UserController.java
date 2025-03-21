package com.happiness.conan.web.controller;

import com.happiness.conan.common.BaseResponse;
import com.happiness.conan.common.Version;
import com.happiness.conan.service.UserService;
import com.happiness.conan.web.dto.CreateUserRequest;
import com.happiness.conan.web.dto.UserResponse;
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
    @Version(1)
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
