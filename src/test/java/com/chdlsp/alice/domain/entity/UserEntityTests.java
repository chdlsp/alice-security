package com.chdlsp.alice.domain.entity;

import org.junit.Test;

public class UserEntityTests {

    @Test
    public void accessToken() {
        UserEntity userEntity = UserEntity.builder().password("ACCESSTOKEN").build();
    }

}