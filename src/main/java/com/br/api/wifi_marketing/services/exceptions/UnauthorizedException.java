package com.br.api.wifi_marketing.services.exceptions;

public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(Object obj) {
        super("Authentication required: " + obj);
    }
}
