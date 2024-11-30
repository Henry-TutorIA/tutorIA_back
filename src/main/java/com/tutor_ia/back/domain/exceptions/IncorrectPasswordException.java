package com.tutor_ia.back.domain.exceptions;

import org.springframework.http.HttpStatus;

public class IncorrectPasswordException extends ApiException {

    public IncorrectPasswordException() {
        super(HttpStatus.BAD_REQUEST, "Incorrect Password");
    }
}
