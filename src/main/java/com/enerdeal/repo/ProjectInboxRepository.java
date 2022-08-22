package com.enerdeal.repo;

import com.enerdeal.model.ProjectInbox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface ProjectInboxRepository extends JpaRepository<ProjectInbox, Long> {


    @Query("SELECT p FROM ProjectInbox p WHERE ((:userId IS NULL) OR (:userId IS NOT NULL AND p.userId =:userId))" +
            " AND ((:adminId IS NULL) OR (:adminId IS NOT NULL AND p.adminId = :adminId))"+
            " AND ((:developerId IS NULL) OR (:developerId IS NOT NULL AND p.developerId = :developerId))"+
            " AND ((:projectId IS NULL) OR (:projectId IS NOT NULL AND p.projectId = :projectId))"+
            " AND ((:investorId IS NULL) OR (:investorId IS NOT NULL AND p.investorId = :investorId))"+
            "AND ((:startDate IS NULL) OR (:startDate IS NOT NULL AND p.createdDate >= :startDate)) " +
            "AND ((:endDate IS NULL) OR (:endDate IS NOT NULL AND p.createdDate >= :endDate)) " +
            " AND ((:investorUserId IS NULL) OR (:investorUserId IS NOT NULL AND p.investorUserId = :investorUserId)) order by p.id DESC")
    Page<ProjectInbox> findInboxes(@Param("userId")Long userId,
                         @Param("adminId")Long adminId,
                         @Param("developerId")Long developerId,
                         @Param("projectId")Long projectId,
                         @Param("investorId")Long investorId,
                         @Param("startDate")LocalDateTime startDate,
                         @Param("endDate")LocalDateTime endDate,
                         @Param("investorUserId")Long investorUserId,
                         Pageable pageable);






}
