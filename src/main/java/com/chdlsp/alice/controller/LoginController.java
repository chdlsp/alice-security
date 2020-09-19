package com.chdlsp.alice.controller;

import com.chdlsp.alice.config.SocialApiConfig;
import com.chdlsp.alice.domain.entity.UserLoginHistory;
import com.chdlsp.alice.interfaces.vo.KakaoUserInfoVO;
import com.chdlsp.alice.service.KakaoAuthApiService;
import com.chdlsp.alice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.swing.text.html.Option;
import java.util.Optional;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    KakaoAuthApiService kakaoAuthApiService;

    @Autowired
    UserService userService;

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
    public String loginMain(Model model) {

        log.info("redirectUri: {}, appKey: {}", redirectUri, appKey);
        model.addAttribute("redirectUri", redirectUri);
        model.addAttribute("appKey", appKey);

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        log.info("logout info : " + session.getAttributeNames());

        // Kakao 로그아웃 수행
        kakaoAuthApiService.doKakaoLogout((String)session.getAttribute("access_Token"));

        log.info("logout info : " + (Long) session.getAttribute("login_history_id"));

        // Logout 이력 적재
        userService.registerUserLogoutInfo((Long) session.getAttribute("login_history_id"));

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
    public String loginWithUserEmail(@RequestParam("code") String code, HttpSession session) {

        // code 를 이용해 AccessToken 확인
        String accessToken = kakaoAuthApiService.getAccessTokenUsingCode(code);
        log.info("accessToken : " + accessToken);

        // TODO 개선필요
        // KakaoUserInfoVO kakaoUserInfoVO = kakaoAuthApiService.getKakaoUserInfo(accessToken);

        // accessToken 을 이용해 Kakao 사용자 정보 확인
        KakaoUserInfoVO kakaoUserInfo = kakaoAuthApiService.getKakaoUserInfo(accessToken);

        String email = kakaoUserInfo.getEmail();

        // Email 이 기 등록된 User 인지 확인
        boolean isExistedUser = kakaoAuthApiService.isExistedUser(email);

        // email 이 존재하지 않으면 회원가입 처리
        if(!isExistedUser) {
            // TODO: email, name, password 를 임시로 email 로 처리, 추후 수정 필요
            userService.registerUser(kakaoUserInfo.getEmail(), kakaoUserInfo.getEmail(), kakaoUserInfo.getEmail());
        }

        session.setAttribute("email", kakaoUserInfo.getEmail());
        session.setAttribute("nickname", kakaoUserInfo.getNickname());
        session.setAttribute("access_Token", accessToken);

        // 로그인 이력 적재
        UserLoginHistory userLoginHistory = userService.registerUserLoginInfo(email);
        session.setAttribute("login_history_id", userLoginHistory.getId());

        log.info("session : " + session.getAttribute("email"));

        return "hello";
    }


}
