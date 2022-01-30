package com.tasklist.model;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class RegistrationForm {

    private String username;
    private String password;
    private String name;
    private String email;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(
                username, passwordEncoder.encode(password),
                name, email);
    }

}
