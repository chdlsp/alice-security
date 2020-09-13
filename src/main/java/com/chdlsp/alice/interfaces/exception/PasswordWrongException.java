package com.chdlsp.alice.interfaces.exception;

public class PasswordWrongException extends RuntimeException{
    public PasswordWrongException() {
        super("Password is wrong");
    }
}
