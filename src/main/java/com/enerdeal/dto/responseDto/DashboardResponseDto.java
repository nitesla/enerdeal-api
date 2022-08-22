package com.enerdeal.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponseDto {
    private Long developerId;
    private Long InvestorId;
    private Integer totalProject;
    private Integer publishedProject;
    private Integer approvedProject;
    private Integer pendingProject;
    private Integer acceptedProject;
    private Integer declinedProject;
}
