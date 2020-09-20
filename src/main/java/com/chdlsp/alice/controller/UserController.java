package com.chdlsp.alice.controller;

import com.chdlsp.alice.domain.entity.UserEntity;
import com.chdlsp.alice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity create(
            @RequestBody UserEntity resource
    ) throws URISyntaxException {

        String email = resource.getEmail();
        String name = resource.getName();
        String password = resource.getPassword();

        UserEntity userEntity = userService.registerUser(email, name, password);

        String url = "/users/" + userEntity.getId();
        return ResponseEntity.created(new URI(url)).body("{}");
    }
}
