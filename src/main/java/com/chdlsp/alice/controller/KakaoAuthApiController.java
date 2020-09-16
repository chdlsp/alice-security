package com.chdlsp.alice.controller;

import com.chdlsp.alice.config.SocialApiConfig;
import com.chdlsp.alice.interfaces.enums.PropertyKeyEnum;
import com.chdlsp.alice.interfaces.exception.AccessTokenProcessException;
import com.chdlsp.alice.interfaces.vo.AuthTokenRequestVO;
import com.chdlsp.alice.interfaces.vo.AuthTokenResponseVO;
import com.chdlsp.alice.service.KakaoAuthApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;

@Slf4j
@Controller
@RequestMapping("/oauth2")
public class KakaoAuthApiController {

    private KakaoAuthApiService kakaoAuthApiService;

    private final String redirectUri;
    private final String appKey;
    private final String clientSecret;

    public KakaoAuthApiController(SocialApiConfig socialApiConfig, KakaoAuthApiService kakaoAuthApiService) {
        this.redirectUri = socialApiConfig.getRedirectUri();
        this.appKey = socialApiConfig.getAppKey();
        this.clientSecret = socialApiConfig.getClientSecret();
        this.kakaoAuthApiService = kakaoAuthApiService;
    }

    // 사용자 토큰 요청
    @RequestMapping(value = "/token", produces = "application/json")
    public ResponseEntity token(String code) throws JsonProcessingException {

        AuthTokenRequestVO authTokenRequestVO = new AuthTokenRequestVO(code);

        authTokenRequestVO.setClientId(appKey);
        authTokenRequestVO.setRedirectUri(redirectUri);
        authTokenRequestVO.setClientSecret(clientSecret);

        RestTemplate restTemplate = new RestTemplateBuilder().build();

        if (StringUtils.isEmpty(authTokenRequestVO.getClientId()) || StringUtils.isEmpty(authTokenRequestVO.getRedirectUri())
                || StringUtils.isEmpty(authTokenRequestVO.getCode())) {
            return ResponseEntity.badRequest().body("필수 파라미터 불충족");
        }

        // code 를 이용해 accessToken 얻어오기
        HttpHeaders header = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(header);

        UriComponents builder = UriComponentsBuilder.newInstance()
                .scheme("https").host("kauth.kakao.com")
                .path("/oauth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("code", authTokenRequestVO.getCode())
                .queryParam("redirect_uri", authTokenRequestVO.getRedirectUri())
                .queryParam("client_id", authTokenRequestVO.getClientId())
                .queryParam("client_secret", authTokenRequestVO.getClientSecret())
                .build();

        ResponseEntity<AuthTokenResponseVO> resultAuthTokenResponseVO;

        try {
            resultAuthTokenResponseVO = restTemplate.exchange(builder.toString(), HttpMethod.POST, entity, AuthTokenResponseVO.class);
        } catch (Exception e) {
            throw new AccessTokenProcessException();
        }

        // AccessToken 을 받아온 경우 사용자 정보 요청
        String accessToken = resultAuthTokenResponseVO.getBody().getAccess_token();

        // Email 조회
        String userEmail = kakaoAuthApiService.getEmailUsingAccessToken(accessToken);

        log.info("response : " + resultAuthTokenResponseVO);

        return ResponseEntity.ok().body(resultAuthTokenResponseVO);

    }

    // access token 정보 요청
    @GetMapping(value = "/access_token_info", produces = "application/text")
    public ResponseEntity accessTokenInfo(@RequestParam String accessToken) {

        log.info("accessToken : {} " + accessToken);

        RestTemplate restTemplate = new RestTemplateBuilder().build();

        UriComponents builder = UriComponentsBuilder.newInstance()
                .scheme("https").host("kapi.kakao.com")
                .path("v1/user/access_token_info")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity request = new HttpEntity(headers);

        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                request,
                String.class
        );

    }

    // 요청 'property_keys' 에 따른 사용자 정보 요청
    @GetMapping(value = "/users")
    public ResponseEntity user(@RequestParam String accessToken, @RequestParam(required = false) PropertyKeyEnum[] propertyKeys) {

        RestTemplate restTemplate = new RestTemplateBuilder().build();

        ArrayList<String> properties = new ArrayList<>();

        // enum 타입을 string 으로 바꿔서 arrayList 에 저장
        for (PropertyKeyEnum p : propertyKeys) {
            properties.add(p.value());
        }

        // 헤더에 accessToken 담기 (Admin 키도 가능)
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // 요청 uri 생성
        UriComponents builder = UriComponentsBuilder.newInstance()
                .scheme("https").host("kapi.kakao.com")
                .path("v2/user/me")
                .queryParam("property_keys", new Gson().toJson(properties))
                .build();

        log.info("Request URI: {}", builder.toUriString());

        HttpEntity request = new HttpEntity(headers);

        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                request,
                String.class
        );
    }
}
