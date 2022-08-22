package com.enerdeal.controller;

import com.enerdeal.dto.responseDto.DashboardResponseDto;
import com.enerdeal.dto.responseDto.Response;
import com.enerdeal.service.DashboardService;
import com.enerdeal.utils.Constants;
import com.enerdeal.utils.CustomResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("All")
@RestController
@RequestMapping(Constants.APP_CONTENT+"dashboard")
public class DashboardController {

    @Autowired
    private DashboardService service;


    @GetMapping("/developer")
    public ResponseEntity<Response> getDeveloperSummary(@RequestParam(value = "developerId") Long developerId){
        HttpStatus httpCode ;
        Response resp = new Response();
        DashboardResponseDto response = service.getDeveloperSummary(developerId);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }


    @GetMapping("/investor")
    public ResponseEntity<Response> getInvestorSummary(@RequestParam(value = "investorId") Long investorId){
        HttpStatus httpCode ;
        Response resp = new Response();
        DashboardResponseDto response = service.getInvestorSummary(investorId);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

    @GetMapping("/admin")
    public ResponseEntity<Response> getAdminSummary(){
        HttpStatus httpCode ;
        Response resp = new Response();
        DashboardResponseDto response = service.getAdminSummary();
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

}
