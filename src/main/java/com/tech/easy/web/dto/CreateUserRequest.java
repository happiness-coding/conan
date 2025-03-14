package com.tech.easy.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    private String email;

    public CreateUserRequest() {}
    public CreateUserRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
