package com.enerdeal.dto.requestDto;

import lombok.Data;

@Data
public class AdminUserSignUpDto {

    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private String password;
}
