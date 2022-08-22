package com.enerdeal.notification.requestDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VoiceOtpRequest {

    private String message;
    private String phoneNumber;
    private String pinCode;

}
