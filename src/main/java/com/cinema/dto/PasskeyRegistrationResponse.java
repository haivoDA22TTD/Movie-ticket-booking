package com.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyRegistrationResponse {
    private String challenge;
    private String userId;
    private String userName;
    private String rpId;
    private String rpName;
    private Long timeout;
}
