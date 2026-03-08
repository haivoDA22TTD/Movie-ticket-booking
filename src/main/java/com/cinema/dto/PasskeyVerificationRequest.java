package com.cinema.dto;

import lombok.Data;

@Data
public class PasskeyVerificationRequest {
    private String credentialId;
    private String clientDataJSON;
    private String authenticatorData;
    private String signature;
    private String userHandle;
}
