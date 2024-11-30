package com.tutor_ia.back.domain.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {

    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "user not found");
    }
}
