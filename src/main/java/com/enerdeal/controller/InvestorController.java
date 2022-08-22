package com.enerdeal.controller;

import com.enerdeal.dto.requestDto.ChangePasswordDto;
import com.enerdeal.dto.requestDto.EnableDisableDto;
import com.enerdeal.dto.requestDto.InvestorRequest;
import com.enerdeal.dto.requestDto.InvestorSignUpRequest;
import com.enerdeal.dto.responseDto.InvestorActivationResponse;
import com.enerdeal.dto.responseDto.InvestorResponse;
import com.enerdeal.dto.responseDto.InvestorSignUpResponse;
import com.enerdeal.dto.responseDto.Response;
import com.enerdeal.model.Investor;
import com.enerdeal.service.InvestorService;
import com.enerdeal.utils.Constants;
import com.enerdeal.utils.CustomResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("All")
@RestController
@RequestMapping(Constants.APP_CONTENT+"investor")
public class InvestorController {

    @Autowired
    private InvestorService service;


    @PostMapping("/signup")
    public ResponseEntity<Response> InvestorSignUp(@Validated @RequestBody InvestorSignUpRequest request, HttpServletRequest request1){
        HttpStatus httpCode ;
        Response resp = new Response();
        InvestorSignUpResponse response = service.InvestorSignUp(request,request1);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        resp.setData(response);
        httpCode = HttpStatus.CREATED;
        return new ResponseEntity<>(resp, httpCode);
    }


    @PutMapping("/passwordactivation")
    public ResponseEntity<Response> InvestorPasswordActivation(@Valid @RequestBody ChangePasswordDto request){
        HttpStatus httpCode ;
        Response resp = new Response();
        InvestorActivationResponse response = service.InvestorPasswordActivation(request);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Password changed successfully");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }


    @PostMapping("")
    // @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_CREATE_USER')")
    public ResponseEntity<Response> createInvestorProperties(@Valid @RequestBody InvestorRequest request){
        HttpStatus httpCode ;
        Response resp = new Response();
        InvestorResponse response = service.createInvestorProperties(request);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        resp.setData(response);
        httpCode = HttpStatus.CREATED;
        return new ResponseEntity<>(resp, httpCode);
    }


    @PutMapping("")
    // @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_CREATE_USER')")
    public ResponseEntity<Response> updateInvestorProperties(@Validated @RequestBody  InvestorRequest request,HttpServletRequest request1){
        HttpStatus httpCode ;
        Response resp = new Response();
        InvestorResponse response = service.updateInvestorProperties(request,request1);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Update Successful");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }



    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_CREATE_USER')")
    public ResponseEntity<Response> getInvestorProperty(@PathVariable Long id){
        HttpStatus httpCode ;
        Response resp = new Response();
        InvestorResponse response = service.findInvestorAsset(id);
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
        List<Investor> response = service.getAll(isActive);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

    @GetMapping("")
    public ResponseEntity<Response> getInvestors(@RequestParam(value = "name",required = false)String name,
                                                 @RequestParam(value = "page") int page,
                                                 @RequestParam(value = "sortBy", required = false) String sort,
                                                 @RequestParam(value = "pageSize") int pageSize){
        HttpStatus httpCode ;
        Response resp = new Response();
        Sort sortType = (sort != null && sort.equalsIgnoreCase("asc"))
                ?  Sort.by(Sort.Order.asc("id")) :   Sort.by(Sort.Order.desc("id"));
        Page<Investor> response = service.findAll(name, PageRequest.of(page, pageSize, sortType));
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

}
