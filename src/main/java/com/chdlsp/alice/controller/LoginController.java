package com.chdlsp.alice.controller;

import com.chdlsp.alice.config.SocialApiConfig;
import com.chdlsp.alice.service.KakaoAuthApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    KakaoAuthApiService kakaoAuthApiService;

    private final String redirectUri;
    private final String appKey;
    private final String clientSecret;

    public LoginController(SocialApiConfig socialApiConfig) {
        this.redirectUri = socialApiConfig.getRedirectUri();
        this.appKey = socialApiConfig.getAppKey();
        this.clientSecret = socialApiConfig.getClientSecret();
    }

    @GetMapping({"", "/"})
    public String getAuthorizationMessage() {
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model) {

        log.info("redirectUri: {}, appKey: {}", redirectUri, appKey);
        model.addAttribute("redirectUri", redirectUri);
        model.addAttribute("appKey", appKey);

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        // 로그아웃 수행
        kakaoAuthApiService.doKakaoLogout((String)session.getAttribute("access_Token"));

        session.removeAttribute("access_Token");
        session.removeAttribute("email");

        return "login";
    }

    @GetMapping({"/loginSuccess", "/hello"})
    public String loginSuccess() {
        return "hello";
    }

    @GetMapping("/loginFailure")
    public String loginFailure() {
        return "error";
    }

    @GetMapping(value = "/oauth2/token", produces = "application/json")
    public String validateUserEmail(@RequestParam("code") String code, HttpSession session) {

        // code 를 이용해 AccessToken 확인
        String accessToken = kakaoAuthApiService.getAccessTokenUsingCode(code);
        log.info("accessToken : " + accessToken);

        // @TODO 개선필요
        //  KakaoUserInfoVO kakaoUserInfoVO = kakaoAuthApiService.getKakaoUserInfo(accessToken);

        // accessToken 을 이용해 Kakao 사용자 정보 확인
        String email = kakaoAuthApiService.getKakaoUserInfo(accessToken);

        // 클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
        if (email != null) {
            session.setAttribute("email", email);
            session.setAttribute("access_Token", accessToken);
        }

        log.info("session : " + session.getAttribute("email"));

        return "hello";
    }


}
