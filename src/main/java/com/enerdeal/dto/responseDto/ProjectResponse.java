package com.enerdeal.dto.responseDto;

import com.enerdeal.enums.ApprovalStatus;
import com.enerdeal.enums.InvestorStatus;
import com.enerdeal.model.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResponse {
    private Long id;
    private Long developerId;
    private Long investorId;
    private String name;
    private String clientName;
    private String location;
    private String clientIndustry;
//    private MountType mountType;
//    private Integer totalLandSpace;
//    private Integer individualLandSpace;
//    private Integer kwpPanels;
    private Boolean isPublished;
    private ApprovalStatus approvalStatus;
    private InvestorStatus investorStatus;
    private String coordinates;
    private List<BuildingPicture> buildingPictures;
    private List<LandPicture> landPictures;
    private List<GeneratorPicture> generatorPictures;
    private List<CentralDistributionBoardPicture> centralDistributionBoardPictures;
    private List<EquipmentNameplatePicture> equipmentNameplatePictures;
    private String utilityBill;
    private String simulation;
    private String systemSizingDocument;
    private String benchmarkEPC;
    private String replacementCapex;
    private String replacementCapexYear;
    private String oAndMCharge;
    private String plantCapacity;
    private String solarIrradiationYield;
    private String annualDegredation;
    private String plantExpectedUtilization;
    private String ppaTenure;
//    private String fixedDevelopmentFee;
    private BigDecimal tariff;
    private String tariffEscalation;
    private String expectedConstructionPeriod;
    private String auditedFinancials;
    private LocalDateTime registrationDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isActive;
    private String developerName;
    private String investorName;
    private BigDecimal totalCapitalRequired;
}
