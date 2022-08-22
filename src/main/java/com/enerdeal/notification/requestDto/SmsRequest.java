package com.enerdeal.notification.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SmsRequest {
    private String message;
    private String phoneNumber;
    private String pinCode;
}
