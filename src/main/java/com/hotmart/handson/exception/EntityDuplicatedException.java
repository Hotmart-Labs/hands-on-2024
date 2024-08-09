package com.hotmart.handson.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityDuplicatedException extends ResponseStatusException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE_TEMPLATE = "%s already exists";

    public EntityDuplicatedException(String resource) {
        super(HttpStatus.CONFLICT, String.format(MESSAGE_TEMPLATE, resource));
    }

    public EntityDuplicatedException(String resource, Throwable cause) {
        super(HttpStatus.CONFLICT, String.format(MESSAGE_TEMPLATE, resource), cause);
    }
}
