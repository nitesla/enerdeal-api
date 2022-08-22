package com.enerdeal.dto.requestDto;

import lombok.Data;

@Data
public class ChangePasswordDto {

    private Long id;
    private String password;
    private String previousPassword;
}
