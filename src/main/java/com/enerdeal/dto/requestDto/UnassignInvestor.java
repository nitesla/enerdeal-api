package com.enerdeal.dto.requestDto;

import lombok.Data;

import java.util.List;


@Data
public class UnassignInvestor {
    private List<Long> projectId;
}
