package com.chdlsp.alice.interfaces.exception;

public class EmailExistedException extends RuntimeException{

    public EmailExistedException(String email) {
        super("Existed email " + email);
    }

}
