package com.enerdeal.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestorRequest {
    private Long id;
    private String rcNumber;
    private String address;
    private String companyName;
    private String phone;
    private String email;
    private String registrationToken;
    private String registrationTokenExpiration;
}
