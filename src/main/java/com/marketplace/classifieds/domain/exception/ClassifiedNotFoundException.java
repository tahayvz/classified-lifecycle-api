package com.marketplace.classifieds.domain.exception;

public class ClassifiedNotFoundException extends RuntimeException {

    public ClassifiedNotFoundException(String message) {
        super(message);
    }
}
