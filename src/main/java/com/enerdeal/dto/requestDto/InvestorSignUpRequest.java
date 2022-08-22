package com.enerdeal.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvestorSignUpRequest {
    //Organisation's Name
    //Designation in the organisation
    //Organisation's Address
    //Organisation's Website
    //RC Number
    //CEO's Name
    //Official Phone Number
    //Official Email Address

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String companyName;
    private String address;
}
