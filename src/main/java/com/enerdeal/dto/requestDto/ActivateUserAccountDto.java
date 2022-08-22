package com.enerdeal.dto.requestDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivateUserAccountDto {


    private String resetToken;

    private Long updatedBy;
    private Boolean isActive;
    private LocalDateTime passwordChangedOn;


}
