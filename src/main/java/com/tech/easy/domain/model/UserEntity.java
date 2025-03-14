package com.tech.easy.domain.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String email;

    public UserEntity() {}
    public UserEntity(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
