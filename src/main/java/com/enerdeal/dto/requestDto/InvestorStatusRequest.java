package com.enerdeal.dto.requestDto;

import com.enerdeal.enums.InvestorStatus;
import lombok.Data;


@Data
public class InvestorStatusRequest {
    private Long projectId;
    private Long investorId;
    private InvestorStatus investorStatus;
}
