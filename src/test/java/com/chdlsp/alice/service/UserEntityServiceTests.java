package com.chdlsp.alice.service;

import com.chdlsp.alice.domain.entity.UserEntity;
import com.chdlsp.alice.domain.repository.UserRepository;
import com.chdlsp.alice.interfaces.exception.EmailExistedException;
import com.chdlsp.alice.interfaces.exception.EmailNotExistedException;
import com.chdlsp.alice.interfaces.exception.PasswordWrongException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class UserEntityServiceTests {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    public void registerUser() {

        String email = "tester@example.com";
        String name = "Tester";
        String password = "test";

        userService.registerUser(email, name, password);

        verify(userRepository).save(any());
    }

    @Test(expected= EmailExistedException.class)
    public void registerUserWithExistedEmail() {

        String email = "tester@example.com";
        String name = "Tester";
        String password = "test";

        UserEntity mockUserEntity = UserEntity.builder().build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUserEntity));
        userService.registerUser(email, name, password);

        verify(userRepository, never()).save(any()); // 이미 있는 user 면 실행하지 않음
    }

    @Test(expected = EmailNotExistedException.class)
    public void authenticateWithNotExistedEmail() {

        String email = "tester@example.com";
        String password = "test";

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        UserEntity userEntity = userService.authenticate(email, password);

        assertThat(userEntity.getEmail(), is(email));
    }


    @Test(expected = PasswordWrongException.class)
    public void authenticateWithWrongPassword() {

        String email = "tester@example.com";
        String password = "tset";

        UserEntity mockUserEntity = UserEntity.builder()
                .email(email)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUserEntity));
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        UserEntity userEntity = userService.authenticate(email, password);

        assertThat(userEntity.getEmail(), is(email));
    }
}