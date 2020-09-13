package com.chdlsp.alice.controller;

import com.chdlsp.alice.domain.entity.User;
import com.chdlsp.alice.interfaces.util.JwtUtil;
import com.chdlsp.alice.interfaces.vo.SessionResponseVO;
import com.chdlsp.alice.interfaces.vo.SessionRequestVO;
import com.chdlsp.alice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class SessionController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/session")
    public ResponseEntity<SessionResponseVO> create(
            @RequestBody SessionRequestVO resource
    ) throws URISyntaxException {

        String url = "/session";

        String email = resource.getEmail();
        String password = resource.getPassword();

        User user = userService.authenticate(email, password);

        String accessToken = jwtUtil.createToken(user.getId(), user.getName());

        SessionResponseVO sessionResponseVO = SessionResponseVO.builder()
                .accessToken(accessToken)
                .build();

        return ResponseEntity.created(new URI(url)).body(sessionResponseVO);
    }
}
