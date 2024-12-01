package com.tutor_ia.back.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidValueException extends ApiException {

    public InvalidValueException(String value) {
        super(HttpStatus.BAD_REQUEST, String.format("Invalid value: %s", value));
    }
}
