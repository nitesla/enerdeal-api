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
public class DeveloperActivationResponse {

    private Long userId;
    private Long developerId;
    private String phone;
    private String email;

}
