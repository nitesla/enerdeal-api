package com.enerdeal.dto.responseDto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeveloperSignUpResponseDto {
    private Long id;
    private Long developerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String companyName;
    private String username;
}
