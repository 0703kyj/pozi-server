package com.pozi.common.exception.badrequest;

import com.pozi.common.exception.PoziException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends PoziException {
    public BadRequestException(String message, int code) {
        super(HttpStatus.BAD_REQUEST, message, code);
    }
}
