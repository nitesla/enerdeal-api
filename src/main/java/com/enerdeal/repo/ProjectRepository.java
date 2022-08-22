package com.enerdeal.repo;

import com.enerdeal.enums.ApprovalStatus;
import com.enerdeal.enums.InvestorStatus;
import com.enerdeal.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByName(String name);


    Integer countAllByDeveloperId(Long developerId);
    Integer countAllByDeveloperIdAndIsPublished(Long developerId, boolean isPublished);
    Integer countAllByDeveloperIdAndApprovalStatus(Long developerId, ApprovalStatus approvalStatus);


    Integer countAllByInvestorId(Long investorId);
    Integer countAllByInvestorIdAndInvestorStatus(Long investorId, InvestorStatus investorStatus);


    Integer countAllByIsPublished(boolean isPublished);
    Integer countAllByApprovalStatus(ApprovalStatus approvalStatus);


    @Query("SELECT p FROM Project p WHERE ((:name IS NULL) OR (:name IS NOT NULL AND p.name like %:name%))" +
            " AND ((:investorId IS NULL) OR (:investorId IS NOT NULL AND p.investorId = :investorId))"+
            " AND ((:developerId IS NULL) OR (:developerId IS NOT NULL AND p.developerId = :developerId))"+
            " AND ((:id IS NULL) OR (:id IS NOT NULL AND p.id = :id))"+
            " AND ((:approvalStatus IS NULL) OR (:approvalStatus IS NOT NULL AND p.approvalStatus = :approvalStatus))"+
            " AND ((:isPublished IS NULL) OR (:isPublished IS NOT NULL AND p.isPublished = :isPublished)) order by p.id desc")
    Page<Project> findProjects(@Param("name")String name,
                                @Param("investorId")Long investorId,
                                @Param("developerId")Long developerId,
                                @Param("id")Long id,
                                @Param("isPublished")Boolean isPublished,
                                @Param("approvalStatus") ApprovalStatus approvalStatus,
                                Pageable pageable);
}
