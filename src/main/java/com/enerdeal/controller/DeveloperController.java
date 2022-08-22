package com.enerdeal.controller;


import com.enerdeal.dto.requestDto.ChangePasswordDto;
import com.enerdeal.dto.requestDto.DeveloperDto;
import com.enerdeal.dto.requestDto.DeveloperSignUpDto;
import com.enerdeal.dto.requestDto.EnableDisableDto;
import com.enerdeal.dto.responseDto.DeveloperActivationResponse;
import com.enerdeal.dto.responseDto.DeveloperResponseDto;
import com.enerdeal.dto.responseDto.DeveloperSignUpResponseDto;
import com.enerdeal.dto.responseDto.Response;
import com.enerdeal.model.Developer;
import com.enerdeal.service.DeveloperService;
import com.enerdeal.utils.Constants;
import com.enerdeal.utils.CustomResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@SuppressWarnings("All")
@RestController
@RequestMapping(Constants.APP_CONTENT+"developer")
public class DeveloperController {

    private final DeveloperService service;

    public DeveloperController(DeveloperService service) {
        this.service = service;
    }



    @PostMapping("/signup")
    public ResponseEntity<Response> developerSignUp(@Validated @RequestBody DeveloperSignUpDto request, HttpServletRequest request1){
        HttpStatus httpCode ;
        Response resp = new Response();
        DeveloperSignUpResponseDto response = service.developerSignUp(request,request1);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        resp.setData(response);
        httpCode = HttpStatus.CREATED;
        return new ResponseEntity<>(resp, httpCode);
    }

    @PutMapping("/passwordactivation")
    public ResponseEntity<Response> DeveloperPasswordActivation(@Valid @RequestBody ChangePasswordDto request){
        HttpStatus httpCode ;
        Response resp = new Response();
        DeveloperActivationResponse response = service.developerPasswordActivation(request);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Password changed successfully");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

    @PostMapping("")
    // @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_CREATE_USER')")
    public ResponseEntity<Response> createDeveloperProperties(@Validated @RequestBody DeveloperDto request){
        HttpStatus httpCode ;
        Response resp = new Response();
        DeveloperResponseDto response = service.createDeveloperProperties(request);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        resp.setData(response);
        httpCode = HttpStatus.CREATED;
        return new ResponseEntity<>(resp, httpCode);
    }


    @PutMapping("")
    // @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_CREATE_USER')")
    public ResponseEntity<Response> updateDeveloperProperties(@Validated @RequestBody  DeveloperDto request,HttpServletRequest request1){
        HttpStatus httpCode ;
        Response resp = new Response();
        DeveloperResponseDto response = service.updateDeveloperProperties(request,request1);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Update Successful");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }



    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_CREATE_USER')")
    public ResponseEntity<Response> getDeveloperProperty(@PathVariable Long id){
        HttpStatus httpCode ;
        Response resp = new Response();
        DeveloperResponseDto response = service.findDeveloperAsset(id);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }


    @PutMapping("/enabledisable")
    public ResponseEntity<Response> enableDisEnable(@Validated @RequestBody EnableDisableDto request, HttpServletRequest request1){
        HttpStatus httpCode ;
        Response resp = new Response();
        service.enableDisEnable(request,request1);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }


    @GetMapping("/list")
    public ResponseEntity<Response> getAll(@RequestParam(value = "isActive")Boolean isActive){
        HttpStatus httpCode ;
        Response resp = new Response();
        List<Developer> response = service.getAll(isActive);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

}
