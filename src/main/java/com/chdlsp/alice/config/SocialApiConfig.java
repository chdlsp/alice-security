package com.chdlsp.alice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SocialApiConfig {

    @Value("${kakao.oauth.login.redirectUrl}")
    private String redirectUri;

    @Value("${kakao.oauth.appKey}")
    private String appKey;

    @Value("${kakao.oauth.clientSecret}")
    private String clientSecret;

}
