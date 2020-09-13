package com.chdlsp.alice.interfaces.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionRequestVO {

    String email;
    String password;

}
