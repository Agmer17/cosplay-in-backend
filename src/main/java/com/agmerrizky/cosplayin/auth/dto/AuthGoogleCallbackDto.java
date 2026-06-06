package com.agmerrizky.cosplayin.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthGoogleCallbackDto(
        String sub,
        String name,

        @JsonProperty("given_name") String givenName,

        @JsonProperty("family_name") String familyName,

        String picture,
        String email,

        @JsonProperty("email_verified") Boolean emailVerified) {
}