package com.br.api.wifi_marketing.services.exceptions;

public class ForbiddenException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public ForbiddenException(Object message) {
        super("Access Denied: " + message);
    }

}
