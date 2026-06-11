package com.marketplace.classifieds.domain.exception;

public class ImmutableClassifiedException extends RuntimeException {
    public ImmutableClassifiedException(String message) {
        super(message);
    }
}
