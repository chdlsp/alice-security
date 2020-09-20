package com.chdlsp.alice.controller;

import com.chdlsp.alice.domain.entity.UserEntity;
import com.chdlsp.alice.interfaces.exception.EmailNotExistedException;
import com.chdlsp.alice.interfaces.exception.PasswordWrongException;
import com.chdlsp.alice.interfaces.util.JwtUtil;
import com.chdlsp.alice.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(SessionController.class)
public class SessionControllerTests {

    @Autowired
    MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void createWithValidAttributes() throws Exception {

        Long id = 1004L;
        String email = "tester@example.com";
        String name = "tester";
        String password = "test";

        UserEntity mockUserEntity = UserEntity.builder().password("ACCESSTOKEN").build();

        given(userService.authenticate(email, password)).willReturn(mockUserEntity);

        given(jwtUtil.createToken(id, name)).willReturn("header.payload.signature");

        mvc.perform(post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"tester@example.com\",\"name\":\"Tester\",\"password\":\"test\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/session"))
                .andExpect(content().string(contains(".")))
                .andExpect(content().string(contains("{\"access_token\":\"\"}")));

        verify(userService).authenticate(eq(email), eq(password));
    }

    @Test
    public void createWithWrongPassword() throws Exception {

        given(userService.authenticate("tester@example.com","tset"))
                .willThrow(PasswordWrongException.class);

        mvc.perform(post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"tester@example.com\",\"name\":\"Tester\",\"password\":\"tset\"}"))
                .andExpect(status().isBadRequest());

        verify(userService).authenticate(eq("tester@example.com"), eq("tset"));
    }

    @Test
    public void createWithNotExistedEmail() throws Exception {

        given(userService.authenticate("teste@example.com","test"))
                .willThrow(EmailNotExistedException.class);

        mvc.perform(post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"teste@example.com\",\"name\":\"Tester\",\"password\":\"tset\"}"))
                .andExpect(status().isBadRequest());

        verify(userService).authenticate(eq("teste@example.com"), eq("test"));
    }
}