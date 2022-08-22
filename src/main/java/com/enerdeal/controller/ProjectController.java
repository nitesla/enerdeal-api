package com.enerdeal.controller;

import com.enerdeal.dto.requestDto.*;
import com.enerdeal.dto.responseDto.ProjectResponse;
import com.enerdeal.dto.responseDto.ProjectResponsePage;
import com.enerdeal.dto.responseDto.Response;
import com.enerdeal.enums.ApprovalStatus;
import com.enerdeal.service.ProjectService;
import com.enerdeal.utils.Constants;
import com.enerdeal.utils.CustomResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("All")
@RestController
@RequestMapping(Constants.APP_CONTENT+"project")
public class ProjectController {

    @Autowired
    private ProjectService service;



    /** <summary>
     * Project creation endpoint
     * </summary>
     * <remarks>this endpoint is responsible for creation of new Project</remarks>
     */

    @PostMapping("")
    public ResponseEntity<Response> createProject(@Valid @RequestBody ProjectRequest request){
        HttpStatus httpCode ;
        Response resp = new Response();
        ProjectResponse response = service.createProject(request);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        resp.setData(response);
        httpCode = HttpStatus.CREATED;
        return new ResponseEntity<>(resp, httpCode);
    }


    /** <summary>
     * Project update endpoint
     * </summary>
     * <remarks>this endpoint is responsible for updating Project</remarks>
     */

    @PutMapping("")
    public ResponseEntity<Response> updateProject(@Valid @RequestBody UpdateProjectRequest request){
        HttpStatus httpCode ;
        Response resp = new Response();
        ProjectResponse response = service.updateProject(request);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Update Successful");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }


    /** <summary>
     * Get single record endpoint
     * </summary>
     * <remarks>this endpoint is responsible for getting a single record</remarks>
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response> getProject(@PathVariable Long id){
        HttpStatus httpCode ;
        Response resp = new Response();
        ProjectResponse response = service.findProject(id);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }


    /** <summary>
     * Get all records endpoint
     * </summary>
     * <remarks>this endpoint is responsible for getting all records and its searchable</remarks>
     */
    @GetMapping("")
    public ResponseEntity<Response> getProjects(@RequestParam(value = "name",required = false)String name,
//                                                @RequestParam(value = "ProjectCode",required = false)String ProjectCode,
                                                @RequestParam(value = "investorId",required = false)Long investorId,
                                                @RequestParam(value = "developerId",required = false)Long developerId,
                                                @RequestParam(value = "id",required = false)Long id,
                                                @RequestParam(value = "isPublished",required = false)Boolean isPublished,
                                                @RequestParam(value = "approvalStatus",required = false) ApprovalStatus approvalStatus,
                                                @RequestParam(value = "page") int page,
                                                @RequestParam(value = "sortBy", required = false) String sort,
                                                @RequestParam(value = "pageSize") int pageSize){
        HttpStatus httpCode ;
        Response resp = new Response();
        Sort sortType = (sort != null && sort.equalsIgnoreCase("asc"))
                ?  Sort.by(Sort.Order.asc("id")) :   Sort.by(Sort.Order.desc("id"));
        ProjectResponsePage response = service.findAll(name, investorId, developerId, id, isPublished, approvalStatus, PageRequest.of(page, pageSize, sortType));
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Record fetched successfully !");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }



    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this endpoint is responsible for enabling and disenabling a Project</remarks>
     */

    @PutMapping("/enabledisenable")
    public ResponseEntity<Response> enableDisEnable(@Validated @RequestBody EnableDisableDto request){
        HttpStatus httpCode ;
        Response resp = new Response();
        service.enableDisEnableProject(request);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }



//    @GetMapping("/list")
//    public ResponseEntity<Response> getAll(@RequestParam(value = "isActive")Boolean isActive){
//        HttpStatus httpCode ;
//        Response resp = new Response();
//        List<Project> response = service.getAll(isActive);
//        resp.setCode(CustomResponseCode.SUCCESS);
//        resp.setDescription("Record fetched successfully !");
//        resp.setData(response);
//        httpCode = HttpStatus.OK;
//        return new ResponseEntity<>(resp, httpCode);
//    }


    @PostMapping("/unassignInvestor")
    public ResponseEntity<Response> unassignInvestor(@Validated @RequestBody UnassignInvestor unassignInvestor){
        HttpStatus httpCode ;
        Response resp = new Response();
        List<ProjectResponse> response = service.unassignInvestor(unassignInvestor.getProjectId());
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

    @PutMapping("/assignInvestor")
    public ResponseEntity<Response> assignInvestor(@Validated @RequestBody AssignInvestor assignInvestor){
        HttpStatus httpCode ;
        Response resp = new Response();
        ProjectResponse response = service.assignInvestor(assignInvestor);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

    @PutMapping("/updateinvestorstatus")
    public ResponseEntity<Response> updateInvestorStatus(@Validated @RequestBody InvestorStatusRequest request){
        HttpStatus httpCode ;
        Response resp = new Response();
        ProjectResponse response = service.updateInvestorStatus(request);
        resp.setCode(CustomResponseCode.SUCCESS);
        resp.setDescription("Successful");
        resp.setData(response);
        httpCode = HttpStatus.OK;
        return new ResponseEntity<>(resp, httpCode);
    }

}
