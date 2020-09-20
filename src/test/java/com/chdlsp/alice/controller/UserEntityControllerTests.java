package com.chdlsp.alice.controller;

import com.chdlsp.alice.domain.entity.UserEntity;
import com.chdlsp.alice.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserEntityControllerTests {

    @Autowired
    MockMvc mvc;

    @MockBean
    private UserService userService;

    // INPUT : E-mail, Name, Password
    // 성공 : 201

    @Test
    public void create() throws Exception {

        UserEntity mockUserEntity = UserEntity.builder()
                .id(1004L)
                .email("tester@example.com")
                .name("Tester")
                .password("test")
                .build();

        given(userService.registerUser("tester@example.com", "Tester", "test"))
                .willReturn(mockUserEntity);

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"tester@example.com\",\"name\":\"Tester\",\"password\":\"test\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/users/1004"));

        verify(userService).registerUser(
                eq("tester@example.com"), eq("Tester"), eq("test"));
    }
}