package com.chdlsp.alice.interfaces.util;

import io.jsonwebtoken.Claims;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(JwtUtil.class)
public class JwtUtilTests {

    private static final String SECRET = "1qaz2wsx3edc4rfv5tgb6yhn7ujm8ikk";
    private JwtUtil jwtUtil;

    @Before
    public void setUp() {
      jwtUtil = new JwtUtil(SECRET);
    }

    @Test
    public void createToken() {
        String token = jwtUtil.createToken(1004L, "John");
        assertThat(token, containsString("."));
        System.out.println("token : " + token);
    }

    @Test
    public void getClaims() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEwMDQsInVzZXJOYW1lIjoiSm9obiJ9.iREDW1RSk8EZoEPZ1FHZ2ZJayZZzu23cKCebGmr5BG0";
        Claims claims = jwtUtil.getClaims(token);

        assertThat(claims.get("userId", Long.class), is(1004L));
        assertThat(claims.get("userName"), is("John"));
    }

}