package com.hotmart.handson.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotFoundException extends ResponseStatusException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE_TEMPLATE = "%s not found";

    public EntityNotFoundException(String resource) {
        super(HttpStatus.NOT_FOUND, String.format(MESSAGE_TEMPLATE, resource));
    }

    public EntityNotFoundException(String resource, Throwable cause) {
        super(HttpStatus.NOT_FOUND, String.format(MESSAGE_TEMPLATE, resource), cause);
    }
}
