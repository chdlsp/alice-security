package com.chdlsp.alice.interfaces.exception;

public class AccessTokenProcessException extends RuntimeException {
    public AccessTokenProcessException() {
        super("Getting AccessToken has failed");
    }
}
