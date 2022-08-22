package com.enerdeal.model;

import com.enerdeal.enums.ApprovalStatus;
import com.enerdeal.enums.InvestorStatus;
import com.enerdeal.enums.MountType;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Project extends CoreEntity{

    private Long developerId;
    private Long investorId;
    private String name;
    private String clientName;
    private String location;
    private String clientIndustry;
    @Enumerated(value = EnumType.STRING)
    private MountType mountType;
    private Integer totalLandSpace;
    private Integer individualLandSpace;
    private Integer kwpPanels;
    private Boolean isPublished;
    @Enumerated(value = EnumType.STRING)
    private ApprovalStatus approvalStatus;
    @Enumerated(value = EnumType.STRING)
    private InvestorStatus investorStatus;
    private String coordinates;
//    private Long buildingPictureId;
//    private Long landPictureId;
//    private List<String> generatorPictures;
//    private List<String> centralDistributionBoardPictures;
//    private List<String> equipmentNameplatePictures;
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
    private String fixedDevelopmentFee;
    private BigDecimal tariff;
    private String tariffEscalation;
    private String expectedConstructionPeriod;
    private String auditedFinancials;
    private BigDecimal totalCapitalRequired;

    @Transient
    private String developerName;

    @Transient
    private String investorName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Project project = (Project) o;
        return getId() != null && Objects.equals(getId(), project.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
