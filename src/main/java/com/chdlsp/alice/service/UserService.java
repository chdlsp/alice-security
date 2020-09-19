package com.chdlsp.alice.service;

import com.chdlsp.alice.domain.entity.User;
import com.chdlsp.alice.domain.entity.UserLoginHistory;
import com.chdlsp.alice.domain.repository.UserLoginHistoryRepository;
import com.chdlsp.alice.domain.repository.UserRepository;
import com.chdlsp.alice.interfaces.exception.EmailExistedException;
import com.chdlsp.alice.interfaces.exception.EmailNotExistedException;
import com.chdlsp.alice.interfaces.exception.PasswordWrongException;
import jdk.vm.ci.meta.Local;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserService {

    UserRepository userRepository;
    UserLoginHistoryRepository userLoginHistoryRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserLoginHistoryRepository userLoginHistoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userLoginHistoryRepository = userLoginHistoryRepository;
    }

    // 회원가입 처리
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
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);

    }

    // 로그인 이력 등록
    public UserLoginHistory registerUserLoginInfo(String email) {

        UserLoginHistory userLoginHistory = UserLoginHistory.builder()
                .email(email)
                .createdAt(LocalDateTime.now())
                .build();

        return userLoginHistoryRepository.save(userLoginHistory);

    }

    // 로그아웃 수행
    public void registerUserLogoutInfo(Long id) {

        UserLoginHistory userLoginHistory = UserLoginHistory.builder()
                .id(id)
                .loggedOutAt(LocalDateTime.now())
                .build();

        log.info("userLoginHistory : " + userLoginHistory);
        userLoginHistoryRepository.save(userLoginHistory);

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
