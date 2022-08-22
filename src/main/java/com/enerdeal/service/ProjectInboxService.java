package com.enerdeal.service;


import com.enerdeal.exceptions.NotFoundException;
import com.enerdeal.model.Project;
import com.enerdeal.model.ProjectInbox;
import com.enerdeal.model.User;
import com.enerdeal.repo.ProjectInboxRepository;
import com.enerdeal.repo.ProjectRepository;
import com.enerdeal.utils.CustomResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@SuppressWarnings("ALL")
@Slf4j
@Service
public class ProjectInboxService {


    @Autowired
    private ProjectInboxRepository projectInboxRepository;
    @Autowired
    private ProjectRepository projectRepository;



    public ProjectInbox findInbox(Long id){
        ProjectInbox projectInbox = projectInboxRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested id does not exist!"));
        Project project = projectRepository.findById(projectInbox.getProjectId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested project id does not exist!"));
        projectInbox.setProjectName(project.getName());
        return projectInbox;
    }




    public Page<ProjectInbox> findAdminMessages(Long userId, Long developerId, Long projectId,
                                                Long investorId,LocalDateTime startDate, LocalDateTime endDate,Long investorUserId, PageRequest pageRequest ){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Page<ProjectInbox> projectInboxes = projectInboxRepository.findInboxes(userId,userCurrent.getId(),developerId,projectId,
                investorId,startDate,endDate,investorUserId,pageRequest);
        if(projectInboxes == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return projectInboxes;
    }


    public Page<ProjectInbox> findDeveloperMessages(Long adminId, Long developerId, Long projectId,
                                                Long investorId,LocalDateTime startDate, LocalDateTime endDate,Long investorUserId, PageRequest pageRequest ){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Page<ProjectInbox> projectInboxes = projectInboxRepository.findInboxes(userCurrent.getId(),adminId,developerId,projectId,
                investorId,startDate,endDate,investorUserId,pageRequest);
        if(projectInboxes == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return projectInboxes;
    }



    public Page<ProjectInbox> findInvestorsMessages(Long userId,Long adminId, Long developerId, Long projectId,
                                                    Long investorId,LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest ){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Page<ProjectInbox> projectInboxes = projectInboxRepository.findInboxes(userId,adminId,developerId,projectId,
                investorId,startDate,endDate,userCurrent.getId(),pageRequest);
        if(projectInboxes == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return projectInboxes;
    }
}


