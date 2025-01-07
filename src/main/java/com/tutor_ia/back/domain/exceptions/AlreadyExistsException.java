package com.tutor_ia.back.domain.exceptions;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ApiException {

    public AlreadyExistsException(String className) {
        super(HttpStatus.CONFLICT, className + " already exists");
    }
}
