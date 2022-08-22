package com.enerdeal.dto.requestDto;


import lombok.Data;


@Data
public class DeveloperDto {

    private Long id;
    private String rcNumber;
    private String address;
    private String companyName;
    private String phone;
    private String email;
    private String registrationToken;
    private String registrationTokenExpiration;


}
