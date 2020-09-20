package com.chdlsp.alice.service;

import com.chdlsp.alice.domain.entity.UserEntity;
import com.chdlsp.alice.domain.entity.UserLoginHistoryEntity;
import com.chdlsp.alice.domain.repository.UserLoginHistoryRepository;
import com.chdlsp.alice.domain.repository.UserRepository;
import com.chdlsp.alice.interfaces.exception.EmailExistedException;
import com.chdlsp.alice.interfaces.exception.EmailNotExistedException;
import com.chdlsp.alice.interfaces.exception.PasswordWrongException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public UserEntity registerUser(String email, String name, String password) {

        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent()) {
            throw new EmailExistedException(email);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .name(name)
                .password(encodedPassword)
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(userEntity);

    }

    // 로그인 이력 등록
    public UserLoginHistoryEntity registerUserLoginInfo(String email) {

        // email 로 userEntity 의 id 찾기
        Optional<UserEntity> userByEmail = userRepository.findByEmail(email);

        UserLoginHistoryEntity userLoginHistoryEntity = UserLoginHistoryEntity.builder()
                .email(email)
                .createdAt(LocalDateTime.now())
                .build();

        userByEmail.ifPresent(user -> {
            userLoginHistoryEntity.setUserEntity(userRepository.getOne(user.getId()));
        });

        return userLoginHistoryRepository.save(userLoginHistoryEntity);

    }

    // 로그아웃 수행
    public void registerUserLogoutInfo(Long id) {

        Optional<UserLoginHistoryEntity> selectUserLoginHistory = userLoginHistoryRepository.findById(id);

        selectUserLoginHistory.ifPresent(loginInfo -> {
            loginInfo.setLoggedOutAt(LocalDateTime.now());
            userLoginHistoryRepository.save(loginInfo);
        });

        log.info("userLoginHistory : " + selectUserLoginHistory);
    }

    public UserEntity authenticate(String email, String password) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(EmailNotExistedException::new);

        if(!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new PasswordWrongException();
        }

        return userEntity;
    }

    // 로그인 이력 불러오기
    public List<UserLoginHistoryEntity> getUserLoginHistoryInfo(String email) {

        // email 로 userEntity 의 id 찾기
        Optional<UserEntity> userByEmail = userRepository.findByEmail(email);
        List<UserLoginHistoryEntity> userLoginHistoryList = new ArrayList<>();

        if(userByEmail.isPresent()) {
            userLoginHistoryList = userLoginHistoryRepository.findByUserEntity(userByEmail.get());
        }

        return userLoginHistoryList;
    }
}
