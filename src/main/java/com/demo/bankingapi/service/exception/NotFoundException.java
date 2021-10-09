package com.demo.bankingapi.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// todo create customised error message
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{
    public NotFoundException(Class<?> clazz, Object id) {
        super(String.format("%s: %s not found", clazz.getTypeName(), id));
    }
}
