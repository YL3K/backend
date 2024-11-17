package com.yl3k.kbsf.global.firebase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
public class GoogleOauth2Response {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private String expiresIn;

    private String scope;

    @JsonProperty("token_type")
    private String tokenType;
}
