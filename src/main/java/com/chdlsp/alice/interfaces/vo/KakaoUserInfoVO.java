package com.chdlsp.alice.interfaces.vo;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KakaoUserInfoVO {

    String id;
    String email;
    String nickname;
}
