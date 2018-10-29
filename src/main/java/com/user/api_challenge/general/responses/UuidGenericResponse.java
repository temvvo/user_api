package com.user.api_challenge.general.responses;

import org.springframework.http.HttpStatus;

public class UuidGenericResponse extends GenericApiResponse {
    private String uuid = null;

    public UuidGenericResponse(HttpStatus status, String message, String uuid) {
        super(status, message);
        this.uuid = uuid;
    }
}
