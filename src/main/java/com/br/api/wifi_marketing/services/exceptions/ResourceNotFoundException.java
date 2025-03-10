package com.br.api.wifi_marketing.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(Object obj) {
        super("Resource not found: " + obj);
    }

    public ResourceNotFoundException() {
        super("Resource not found");
    }
}
