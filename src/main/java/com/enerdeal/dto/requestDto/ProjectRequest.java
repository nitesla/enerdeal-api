package com.enerdeal.dto.requestDto;

import com.enerdeal.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {

    @NotNull(message = "Developer id is required")
    private Long developerId;
//    private Long investorId;
    private String name;
    private String clientName;
    private String location;
    private String clientIndustry;
    private String coordinates;
    private List<@URL(message = "Building picture must be a valid url String") String> buildingPictures;
    private List<@URL(message = "Land picture must be a valid url String")String> landPictures;
    private List<@URL(message = "Generator picture must be a valid url String")String> generatorPictures;
    private List<@URL(message = "Central Distribution picture must be a valid url String")String> centralDistributionBoardPictures;
    private List<@URL(message = "Equipment Name Plate picture must be a valid url String")String> equipmentNameplatePictures;
    @URL(message = "Utility bill must be a valid url string")
    private String utilityBill;
    @URL(message = "Simulation must be a valid url string")
    private String simulation;
    @URL(message = "System sizing document be a valid url string")
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
    private BigDecimal tariff;
    private String tariffEscalation;
    private String expectedConstructionPeriod;
    private String auditedFinancials;
    private ApprovalStatus approvalStatus;
    private InvestorRequest investorRequest;
    private Boolean isPublished;
    private BigDecimal totalCapitalRequired;
}
