package com.marketplace.classifieds.domain.exception;

public class DuplicateClassifiedException extends RuntimeException {

    public DuplicateClassifiedException(String message) {
        super(message);
    }
}
