package com.chdlsp.alice.interfaces.exception;

public class EmailNotExistedException extends RuntimeException{
    public EmailNotExistedException() {
        super("Email is not existed");
    }
}
