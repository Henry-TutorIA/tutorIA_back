package com.tutor_ia.back.domain.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus code;

    public ApiException(HttpStatus code, String message) {
        super(message);
        this.code = code;
    }
}
