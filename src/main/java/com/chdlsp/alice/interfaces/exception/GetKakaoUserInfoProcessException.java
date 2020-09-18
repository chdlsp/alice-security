package com.chdlsp.alice.interfaces.exception;

public class GetKakaoUserInfoProcessException extends RuntimeException {
    public GetKakaoUserInfoProcessException() {
        super("Getting Kakao User Infomation has failed");
    }
}
