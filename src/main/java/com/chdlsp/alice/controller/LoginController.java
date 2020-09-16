package com.chdlsp.alice.controller;

import com.chdlsp.alice.config.SocialApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class LoginController {

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

    @GetMapping({"/loginSuccess", "/hello"})
    public String loginSuccess() {
        return "hello";
    }

    @GetMapping("/loginFailure")
    public String loginFailure() {
        return "error";
    }
}
