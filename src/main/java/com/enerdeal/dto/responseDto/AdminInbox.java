package com.enerdeal.dto.responseDto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminInbox {

    private Long id;
    private Long userId;
    private Long adminId;
    private Long developerId;
    private Long projectId;
    private Long investorId;
    private Long investorUserId;
    private String messages;
    private String adminStatus="1";
    private LocalDateTime createdDate;
    private String projectName;
}
