package com.chdlsp.alice.service;

import com.chdlsp.alice.config.SocialApiConfig;
import com.chdlsp.alice.domain.entity.User;
import com.chdlsp.alice.domain.repository.UserRepository;
import com.chdlsp.alice.interfaces.enums.PropertyKeyEnum;
import com.chdlsp.alice.interfaces.exception.AccessTokenProcessException;
import com.chdlsp.alice.interfaces.exception.GetKakaoUserInfoProcessException;
import com.chdlsp.alice.interfaces.vo.AuthTokenRequestVO;
import com.chdlsp.alice.interfaces.vo.AuthTokenResponseVO;
import com.chdlsp.alice.interfaces.vo.KakaoUserInfoVO;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class KakaoAuthApiService {

    @Autowired
    UserRepository userRepository;

    private final String redirectUri;
    private final String appKey;
    private final String clientSecret;

    public KakaoAuthApiService(SocialApiConfig socialApiConfig) {
        this.redirectUri = socialApiConfig.getRedirectUri();
        this.appKey = socialApiConfig.getAppKey();
        this.clientSecret = socialApiConfig.getClientSecret();
    }

    public String getAccessTokenUsingCode(String code) {

        AuthTokenRequestVO authTokenRequestVO = new AuthTokenRequestVO(code);

        authTokenRequestVO.setClientId(appKey);
        authTokenRequestVO.setRedirectUri(redirectUri);
        authTokenRequestVO.setClientSecret(clientSecret);

        RestTemplate restTemplate = new RestTemplateBuilder().build();

        if (StringUtils.isEmpty(authTokenRequestVO.getClientId()) || StringUtils.isEmpty(authTokenRequestVO.getRedirectUri())
                || StringUtils.isEmpty(authTokenRequestVO.getCode())) {
            throw new AccessTokenProcessException();
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
        String accessToken = Objects.requireNonNull(resultAuthTokenResponseVO.getBody()).getAccess_token();

        // TODO 추후 refresh_token 이 필요하다면...
        String refreshToken = Objects.requireNonNull(resultAuthTokenResponseVO.getBody()).getRefresh_token();

        return accessToken;

    }

    // TODO VO로 Return 하게끔 개선 필요
    public KakaoUserInfoVO getKakaoUserInfo(String accessToken) {

        ArrayList<String> properties = new ArrayList<>();

        // enum 타입을 string 으로 바꿔서 arrayList 에 저장
        properties.add(PropertyKeyEnum.KAKAO_ACCOUNT_EMAIL.value());

        // accessToken 을 이용해 사용자 정보 얻어오기
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(header);

        UriComponents builder = UriComponentsBuilder.newInstance()
                .scheme("https").host("kapi.kakao.com")
                .path("/v2/user/me")
                .queryParam("property_keys", new Gson().toJson(properties))
                .build();

        ResponseEntity<String> kakaoUserInfo;

        RestTemplate restTemplate = new RestTemplateBuilder().build();

        // TODO KakaoUserInfoVO 를 개선할 필요가 있음 (우선 구현을 위해 String 으로 진행)
        try {
            kakaoUserInfo = restTemplate.exchange(builder.toString(), HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            throw new GetKakaoUserInfoProcessException();
        }

        JsonElement element = JsonParser.parseString(Objects.requireNonNull(kakaoUserInfo.getBody()));

        JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

        log.info("kakaoAccount : " + kakaoAccount);

        Optional<String> email = Optional.ofNullable(kakaoAccount.getAsJsonObject().get("email").getAsString());
        // TODO: email 외 추가 항목 처리 필요
        // Optional<String> nickname = Optional.ofNullable(kakaoAccount.getAsJsonObject().get("nickname").getAsString());

        log.info("kakaoUserInfoVO Body: " + email);

        KakaoUserInfoVO kakaoUserInfoVO = KakaoUserInfoVO.builder()
                // TODO: builder 패턴 처리 필요
                // .nickname(nickname.toString())
                .email(email.get())
                .build();

        return kakaoUserInfoVO;
    }

    public void doKakaoLogout(String accessToken) {

        // accessToken 을 이용해 사용자 정보 세팅
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(header);

        UriComponents builder = UriComponentsBuilder.newInstance()
                .scheme("https").host("kapi.kakao.com")
                .path("/v1/user/logout")
                .build();

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        ResponseEntity<String> result;

        try {
            result = restTemplate.exchange(builder.toString(), HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            throw new GetKakaoUserInfoProcessException();
        }

        log.info("result : " + result);

    }

    // 기회원 여부 확인
    public boolean isExistedUser(String email) {

        Optional<User> userInfo = userRepository.findByEmail(email);

        if(userInfo.isPresent()) {
            return true;
        } else {
            return false;
        }

    }
}
