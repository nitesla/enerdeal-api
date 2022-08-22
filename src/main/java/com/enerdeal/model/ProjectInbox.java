package com.enerdeal.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class ProjectInbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long adminId;
    private Long developerId;
    private Long projectId;
    private String projectName;
    private Long investorId;
    private Long investorUserId;
    private String investorName;
    private String messages;
    private String adminStatus="1";
    private String developerStatus="1";
    private String investorStatus="1";
    @ApiModelProperty(hidden = true)
    private LocalDateTime createdDate = LocalDateTime.now();



}
