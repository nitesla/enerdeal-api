package com.enerdeal.dto.requestDto;


import lombok.Data;


@Data
public class DeveloperSignUpDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String companyName;
}
