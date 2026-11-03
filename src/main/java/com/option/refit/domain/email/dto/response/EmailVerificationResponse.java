package com.option.refit.domain.email.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmailVerificationResponse(
        @JsonProperty("is_verification")
        boolean isVerification
) {

    public static EmailVerificationResponse of(boolean isVerification) {
        return new EmailVerificationResponse(isVerification);
    }
}
