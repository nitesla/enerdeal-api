package com.enerdeal.controller;


import com.enerdeal.dto.responseDto.Response;
import com.enerdeal.enums.UserCategory;
import com.enerdeal.model.ProjectInbox;
import com.enerdeal.model.User;
import com.enerdeal.service.DeveloperService;
import com.enerdeal.service.ProjectInboxService;
import com.enerdeal.service.TokenService;
import com.enerdeal.utils.Constants;
import com.enerdeal.utils.CustomResponseCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@SuppressWarnings("All")
@RestController
@RequestMapping(Constants.APP_CONTENT+"inbox")
public class ProjectInboxController {

    private final ProjectInboxService service;

    public ProjectInboxController(ProjectInboxService service) {
        this.service = service;
    }







    @GetMapping("/{id}")
    public ResponseEntity<Response> getInbox(@PathVariable Long id){
        HttpStatus httpCode ;
        Response resp = new Response();
        ProjectInbox response = service.findInbox(id);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }


    @GetMapping("")
    public ResponseEntity<Response> getMessages(@RequestParam(value = "userId", required = false) Long userId,
                                                @RequestParam(value = "adminId", required = false) Long adminId,
                                                @RequestParam(value = "developerId", required = false) Long developerId,
                                                @RequestParam(value = "projectId", required = false) Long projectId,
                                                @RequestParam(value = "investorId", required = false) Long investorId,
                                                @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                @RequestParam(value = "investorUserId", required = false) Long investorUserId,
                                                @RequestParam(value = "page") int page,
                                                @RequestParam(value = "pageSize") int pageSize) {

        HttpStatus httpCode;
        Response resp = new Response();
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        if (userCurrent.getUserCategory().equals(UserCategory.DEVELOPER)) {
            Page<ProjectInbox> response = service.findDeveloperMessages(adminId, developerId, projectId, investorId, startDate, endDate, investorUserId, PageRequest.of(page, pageSize));
            resp.setCode(CustomResponseCode.SUCCESS);
            resp.setDescription("Record fetched successfully !");
            resp.setData(response);
            httpCode = HttpStatus.OK;
            return new ResponseEntity<>(resp, httpCode);
        } else if (userCurrent.getUserCategory().equals(UserCategory.INVESTOR)) {
            Page<ProjectInbox> response = service.findInvestorsMessages(userId, adminId, developerId, projectId,
                    investorId, startDate, endDate, PageRequest.of(page, pageSize));
            resp.setCode(CustomResponseCode.SUCCESS);
            resp.setDescription("Record fetched successfully");
            resp.setData(response);
            httpCode = HttpStatus.CREATED;
            return new ResponseEntity<>(resp, httpCode);
        } else {
            Page<ProjectInbox> response = service.findAdminMessages(userId, developerId, projectId,
                    investorId, startDate, endDate, investorUserId, PageRequest.of(page, pageSize));
            resp.setCode(CustomResponseCode.SUCCESS);
            resp.setDescription("Record fetched successfully");
            resp.setData(response);
            httpCode = HttpStatus.CREATED;
            return new ResponseEntity<>(resp, httpCode);
        }
    }
}