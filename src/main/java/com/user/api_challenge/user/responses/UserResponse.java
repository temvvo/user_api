package com.user.api_challenge.user.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.user.api_challenge.general.responses.GenericApiResponse;
import com.user.api_challenge.user.model.User;
import org.springframework.http.HttpStatus;

public class UserResponse extends GenericApiResponse {
    @JsonProperty("user")
    private User data = null;

    public UserResponse(User user) {
        super(HttpStatus.OK);
        this.data = user;
    }

    public UserResponse(User user, String msg) {
        super(HttpStatus.OK, msg);
        this.data = user;
    }
}
