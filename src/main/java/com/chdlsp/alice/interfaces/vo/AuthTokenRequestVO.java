package com.chdlsp.alice.interfaces.vo;

import com.chdlsp.alice.config.SocialApiConfig;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthTokenRequestVO {

    private String grantType = "authorization_code";

    private String clientId;

    private String redirectUri;

    private String code;

    private String clientSecret;

    public AuthTokenRequestVO(String code) {
        this.code = code;
    }
}
