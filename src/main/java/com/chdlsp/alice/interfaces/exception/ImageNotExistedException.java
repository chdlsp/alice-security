package com.chdlsp.alice.interfaces.exception;

public class ImageNotExistedException extends RuntimeException {
    public ImageNotExistedException(String imageName) {
        super("Not Existed Iamge : " + imageName);
    }
}
