package com.chdlsp.alice.interfaces.util;

import com.chdlsp.alice.controller.SessionController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(JwtUtil.class)
public class JwtUtilTests {

    @Test
    public void createToken() {
        String secret = "1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik";
        JwtUtil jwtUtil = new JwtUtil(secret);

        String token = jwtUtil.createToken(1004L, "John");

        assertThat(token, containsString("."));
    }

}