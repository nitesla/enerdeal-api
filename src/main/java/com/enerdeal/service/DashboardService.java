package com.enerdeal.service;

import com.enerdeal.dto.responseDto.DashboardResponseDto;
import com.enerdeal.enums.ApprovalStatus;
import com.enerdeal.enums.InvestorStatus;
import com.enerdeal.repo.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("ALL")
@Service
public class DashboardService {
    @Autowired
    private ProjectRepository projectRepository;


    public DashboardResponseDto getDeveloperSummary(Long developerId) {
        Integer totalProject = projectRepository.countAllByDeveloperId(developerId);
        Integer publishedProject = projectRepository.countAllByDeveloperIdAndIsPublished(developerId, true);
        Integer approvedProject = projectRepository.countAllByDeveloperIdAndApprovalStatus(developerId, ApprovalStatus.APPROVED);
        Integer pendingProject = projectRepository.countAllByDeveloperIdAndApprovalStatus(developerId, ApprovalStatus.PENDING);

        DashboardResponseDto responseDto = new DashboardResponseDto();
        responseDto.setDeveloperId(developerId);
        responseDto.setTotalProject(totalProject);
        responseDto.setPublishedProject(publishedProject);
        responseDto.setApprovedProject(approvedProject);
        responseDto.setPendingProject(pendingProject);

        return responseDto;
    }

    public DashboardResponseDto getInvestorSummary(Long investorId) {
        Integer totalProject = projectRepository.countAllByInvestorId(investorId);
        Integer acceptedProject = projectRepository.countAllByInvestorIdAndInvestorStatus(investorId, InvestorStatus.ACCEPTED);
        Integer declinedProject = projectRepository.countAllByInvestorIdAndInvestorStatus(investorId, InvestorStatus.DECLINED);

        DashboardResponseDto responseDto = new DashboardResponseDto();
        responseDto.setInvestorId(investorId);
        responseDto.setTotalProject(totalProject);
        responseDto.setAcceptedProject(acceptedProject);
        responseDto.setDeclinedProject(declinedProject);

        return responseDto;
    }


    public DashboardResponseDto getAdminSummary() {
        Integer totalProject = (int)projectRepository.count();
        Integer publishedProject = projectRepository.countAllByIsPublished(true);
        Integer approvedProject = projectRepository.countAllByApprovalStatus(ApprovalStatus.APPROVED);
        Integer pendingProject = projectRepository.countAllByApprovalStatus(ApprovalStatus.PENDING);

        DashboardResponseDto responseDto = new DashboardResponseDto();
        responseDto.setTotalProject(totalProject);
        responseDto.setPublishedProject(publishedProject);
        responseDto.setApprovedProject(approvedProject);
        responseDto.setPendingProject(pendingProject);

        return responseDto;
    }

}





