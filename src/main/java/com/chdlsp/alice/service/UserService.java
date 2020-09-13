package com.chdlsp.alice.service;

import com.chdlsp.alice.domain.entity.User;
import com.chdlsp.alice.domain.repository.UserRepository;
import com.chdlsp.alice.interfaces.exception.EmailExistedException;
import com.chdlsp.alice.interfaces.exception.EmailNotExistedException;
import com.chdlsp.alice.interfaces.exception.PasswordWrongException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User registerUser(String email, String name, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent()) {
            throw new EmailExistedException(email);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .name(name)
                .password(encodedPassword)
                .build();

        return userRepository.save(user);

    }

    public User authenticate(String email, String password) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotExistedException());

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordWrongException();
        }

        return user;
    }
}
