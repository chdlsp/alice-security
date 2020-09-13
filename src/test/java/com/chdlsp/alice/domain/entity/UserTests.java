package com.chdlsp.alice.domain.entity;

import org.junit.Test;

public class UserTests {

    @Test
    public void accessToken() {
        User user = User.builder().password("ACCESSTOKEN").build();
    }

}