package com.enerdeal.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequest {


    private String username;
    private String password;






}
