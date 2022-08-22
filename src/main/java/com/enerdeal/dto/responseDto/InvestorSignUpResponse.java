package com.enerdeal.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvestorSignUpResponse {
    private Long id;
    private Long investorId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String companyName;
    private String username;

}
