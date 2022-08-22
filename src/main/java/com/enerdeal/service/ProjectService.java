package com.enerdeal.service;

import com.enerdeal.dto.requestDto.*;
import com.enerdeal.dto.responseDto.ProjectResponse;
import com.enerdeal.dto.responseDto.ProjectResponsePage;
import com.enerdeal.enums.ApprovalStatus;
import com.enerdeal.enums.InvestorStatus;
import com.enerdeal.exceptions.ConflictException;
import com.enerdeal.exceptions.NotFoundException;
import com.enerdeal.helper.Validations;
import com.enerdeal.model.*;
import com.enerdeal.repo.*;
import com.enerdeal.utils.CustomResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.enerdeal.enums.UserCategory.ADMIN;


/**
 * This class is responsible for all business logic for Project
 */


@Slf4j
@SuppressWarnings("ALL")
@Service
public class ProjectService {


    private final ProjectRepository projectRepository;
    @Autowired
    private DeveloperRepository developerRepository;
    @Autowired
    private InvestorRepository investorRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;
    private final ProjectInboxRepository projectInboxRepository;

    @Autowired
    private BuildingPictureService buildingPictureService;

    @Autowired
    private CentralDistributionBoardPictureService centralDistributionBoardPictureService;

    @Autowired
    private  EquipmentNameplatePictureService equipmentNameplatePictureService;

    @Autowired
    private GeneratorPictureService generatorPictureService;

    @Autowired
    private LandPictureService landPictureService;

    @Autowired
    private UserRepository userRepository;

    public ProjectService(ProjectRepository ProjectRepository, ModelMapper mapper,
                          ObjectMapper objectMapper, Validations validations,ProjectInboxRepository projectInboxRepository) {
        this.projectRepository = ProjectRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
        this.projectInboxRepository = projectInboxRepository;
    }

    /**
     * <summary>
     * Project creation
     * </summary>
     * <remarks>this method is responsible for creation of new Projects</remarks>
     */

    public ProjectResponse createProject(ProjectRequest request) {
        validations.validateProject(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Project project = mapper.map(request, Project.class);
        project.setApprovalStatus(ApprovalStatus.PENDING);
        Project ProjectExist = projectRepository.findByName(request.getName());
        if (ProjectExist != null) {
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " project already exist");
        }
        project.setCreatedBy(userCurrent.getId());
        project.setIsActive(true);
        project = projectRepository.save(project);
        List<BuildingPicture> buildingPictures = buildingPictureService.saveBuildingPicture(request.getBuildingPictures(), project.getId());
        List<CentralDistributionBoardPicture> centralDistributionBoardPictures
                = centralDistributionBoardPictureService.saveCentralDistributionBoardPicture(request.getCentralDistributionBoardPictures(), project.getId());
        List<EquipmentNameplatePicture> equipmentNameplatePictures = equipmentNameplatePictureService
                .saveEquipmentNameplatePicture(request.getEquipmentNameplatePictures(), project.getId());
        List<GeneratorPicture> generatorPictures = generatorPictureService.saveBuildingPicture(request.getGeneratorPictures(), project.getId());
        List<LandPicture> landPictures = landPictureService.saveLandPicture(request.getLandPictures(), project.getId());
        log.debug("Create new project - {}" + new Gson().toJson(project));
        ProjectResponse map = mapper.map(project, ProjectResponse.class);
        map.setLandPictures(landPictures);
        map.setGeneratorPictures(generatorPictures);
        map.setBuildingPictures(buildingPictures);
        map.setCentralDistributionBoardPictures(centralDistributionBoardPictures);
        map.setEquipmentNameplatePictures(equipmentNameplatePictures);

        sendToAdmins(project);
        return map;
    }



    /**
     * <summary>
     * Project update
     * </summary>
     * <remarks>this method is responsible for updating already existing Projects</remarks>
     */

    public ProjectResponse updateProject(UpdateProjectRequest request) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();

        Project project = projectRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested project Id does not exist!"));
        Developer developer= developerRepository.findDeveloperById(project.getDeveloperId());
        // Note: investorId is null because the project has not been assigned
//        Investor investor = investorRepository.findInvestorById(project.getInvestorId());
        mapper.map(request, project);
        project.setUpdatedBy(userCurrent.getId());
        Project save = projectRepository.save(project);
        List<BuildingPicture> buildingPictures = buildingPictureService.saveBuildingPicture(request.getBuildingPictures(), project.getId());
        List<CentralDistributionBoardPicture> centralDistributionBoardPictures = centralDistributionBoardPictureService.saveCentralDistributionBoardPicture(request.getCentralDistributionBoardPictures(), project.getId());
        List<EquipmentNameplatePicture> equipmentNameplatePictures = equipmentNameplatePictureService.saveEquipmentNameplatePicture(request.getEquipmentNameplatePictures(), project.getId());
        List<GeneratorPicture> generatorPictures = generatorPictureService.saveBuildingPicture(request.getGeneratorPictures(), project.getId());
        List<LandPicture> landPictures = landPictureService.saveLandPicture(request.getLandPictures(), project.getId());
        log.debug("Create new project - {}" + new Gson().toJson(project));

        if(save.getApprovalStatus().equals(ApprovalStatus.PENDING)){

            ProjectInbox projectInbox= ProjectInbox.builder()
                    .adminId(userCurrent.getId())
                    .projectId(project.getId())
                    .projectName(project.getName())
                    .userId(developer.getUserId())
                    .developerId(project.getDeveloperId())
//                    .investorId(project.getInvestorId())
//                    .investorName(investor.getCompanyName())
                    .createdDate(LocalDateTime.now())
                    .messages(" project " + project.getName() + " is still in review")
                    .build();
            projectInboxRepository.save(projectInbox);

        }
        if(save.getApprovalStatus().equals(ApprovalStatus.APPROVED)){

            ProjectInbox projectInbox= ProjectInbox.builder()
                    .adminId(userCurrent.getId())
                    .projectId(project.getId())
                    .projectName(project.getName())
                    .userId(developer.getUserId())
                    .developerId(project.getDeveloperId())
//                    .investorId(project.getInvestorId())
//                    .investorName(investor.getCompanyName())
                    .createdDate(LocalDateTime.now())
                    .messages(" project " + project.getName() + " has been approved")
                    .build();
            projectInboxRepository.save(projectInbox);

        }
        if(save.getApprovalStatus().equals(ApprovalStatus.DECLINED)){
            ProjectInbox projectInbox= ProjectInbox.builder()
                    .adminId(userCurrent.getId())
                    .projectId(project.getId())
                    .projectName(project.getName())
                    .userId(developer.getUserId())
                    .developerId(project.getDeveloperId())
//                    .investorId(project.getInvestorId())
//                    .investorName(investor.getCompanyName())
                    .createdDate(LocalDateTime.now())
                    .messages(" project " + project.getName() + " has been decline")
                    .build();
            projectInboxRepository.save(projectInbox);
        }
        ProjectResponse map = mapper.map(project, ProjectResponse.class);
        map.setLandPictures(landPictures);
        map.setGeneratorPictures(generatorPictures);
        map.setBuildingPictures(buildingPictures);
        map.setCentralDistributionBoardPictures(centralDistributionBoardPictures);
        map.setEquipmentNameplatePictures(equipmentNameplatePictures);

        return map;
    }

    private ProjectResponse buildProjectProperties(Project save){
        List<BuildingPicture> buildingPictures = buildingPictureService.getBuildingPictures(save.getId());
        List<CentralDistributionBoardPicture> centralDistributionBoardPictures
                = centralDistributionBoardPictureService.getCentralDistributionBoardPictures(save.getId());
        List<EquipmentNameplatePicture> equipmentNameplatePictures = equipmentNameplatePictureService
                .getEquipmentNameplatePictures(save.getId());
        List<GeneratorPicture> generatorPictures = generatorPictureService.getGeneratorPictures(save.getId());
        List<LandPicture> landPictures = landPictureService.getLandPictures(save.getId());
        if(save.getDeveloperId() != null) {
            Developer developer = developerRepository.getOne(save.getDeveloperId());
            save.setDeveloperName(developer.getCompanyName());
        }

        if(save.getInvestorId() != null) {
            Investor investor = investorRepository.getOne(save.getInvestorId());
            save.setInvestorName(investor.getCompanyName());
        }
        log.debug("Building project object - {}" + new Gson().toJson(save));
        ProjectResponse map = mapper.map(save, ProjectResponse.class);
        map.setLandPictures(landPictures);
        map.setGeneratorPictures(generatorPictures);
        map.setBuildingPictures(buildingPictures);
        map.setCentralDistributionBoardPictures(centralDistributionBoardPictures);
        map.setEquipmentNameplatePictures(equipmentNameplatePictures);
        return map;
    }


    /**
     * <summary>
     * Find Project
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public ProjectResponse findProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested project Id does not exist!"));
//        ProjectResponse map = mapper.map(project, ProjectResponse.class);
        return buildProjectProperties(project);
    }


    /**
     * <summary>
     * Find all Project
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public ProjectResponsePage findAll(String name,  Long investorId, Long developerId, Long id, Boolean isPublished, ApprovalStatus approvalStatus, PageRequest pageRequest) {

        Page<Project> project = projectRepository.findProjects(name, investorId, developerId, id, isPublished, approvalStatus,  pageRequest);
        log.info("Project length "+project.getTotalElements());
        if (project == null) {
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        ProjectResponsePage responsePage = new ProjectResponsePage();
        List<ProjectResponse> projectResponses = new ArrayList<>();
        responsePage.setNumber(project.getNumber());
        responsePage.setNumberOfElements(project.getNumberOfElements());
        responsePage.setTotalElements(project.getTotalElements());
        responsePage.setTotalPages(project.getTotalPages());
        project.getContent().forEach(project1 -> {
            ProjectResponse innerProjectResponse = buildProjectProperties(project1);
            projectResponses.add(innerProjectResponse);
        });
        responsePage.setProjectResponse(projectResponses);
        return responsePage;

    }


    /**
     * <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a Project</remarks>
     */
    public void enableDisEnableProject(EnableDisableDto request) {
//            validations.validationsdateStatus(request.getIsActive());
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Project Project = projectRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Project Id does not exist!"));
        Project.setIsActive(request.getIsActive());
        Project.setUpdatedBy(userCurrent.getId());
        projectRepository.save(Project);

    }

    public List<ProjectResponse> unassignInvestor(List<Long> projectId){
        List<ProjectResponse> responseDtos = new ArrayList<>();
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        projectId.forEach(request-> {
            validations.validateProjectId(request);

            Project project = projectRepository.getOne(request);
            Investor investor = investorRepository.findInvestorById(project.getInvestorId());
            Developer developer= developerRepository.findDeveloperById(project.getDeveloperId());
            project.setInvestorId(null);
            project.setInvestorStatus(null);
            project.setUpdatedBy(userCurrent.getId());
            project = projectRepository.save(project);
            ProjectResponse projectResponse = mapper.map(project, ProjectResponse.class);

            responseDtos.add(projectResponse);

            ProjectInbox projectInbox= ProjectInbox.builder()
//                .adminId(userCurrent.getId())
                    .projectId(project.getId())
                    .projectName(project.getName())
                    .userId(developer.getUserId())
                    .developerId(project.getDeveloperId())
                    .investorId(project.getInvestorId())
                    .investorUserId(investor.getUserId())
                    .investorName(investor.getCompanyName())
                    .createdDate(LocalDateTime.now())
                    .messages(" project " + project.getName() + " has been unassigned")
                    .build();
            projectInboxRepository.save(projectInbox);

        });

        return responseDtos;
    }

    public ProjectResponse assignInvestor(AssignInvestor request) {
        validations.validateAssignInvestor(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested project Id does not exist!"));

        Developer developer= developerRepository.findDeveloperById(project.getDeveloperId());

        Investor investor = investorRepository.findInvestorById(request.getInvestorId());
        mapper.map(request, project);
        project.setInvestorStatus(InvestorStatus.PENDING);
        project.setUpdatedBy(userCurrent.getId());
        project = projectRepository.save(project);
        log.debug("Assign investorId to project- {}" + new Gson().toJson(project));

        ProjectInbox projectInbox= ProjectInbox.builder()
//                .adminId(userCurrent.getId())
                .projectId(project.getId())
                .projectName(project.getName())
                .userId(developer.getUserId())
                .developerId(project.getDeveloperId())
                .investorId(project.getInvestorId())
                .investorUserId(investor.getUserId())
                .investorName(investor.getCompanyName())
                .createdDate(LocalDateTime.now())
                .messages(" project " + project.getName() + " has been assigned")
                .build();
        projectInboxRepository.save(projectInbox);
        return mapper.map(project, ProjectResponse.class);
    }

    public ProjectResponse updateInvestorStatus(InvestorStatusRequest request) {
        validations.validateInvestorStatus(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested project Id does not exist!"));
        Investor investor = investorRepository.findInvestorById(project.getInvestorId());
        Developer developer= developerRepository.findDeveloperById(project.getDeveloperId());
        mapper.map(request, project);
        project.setInvestorStatus(request.getInvestorStatus());
        project.setUpdatedBy(userCurrent.getId());
        project = projectRepository.save(project);
        log.debug("investorId to project- {}" + new Gson().toJson(project));

        if(request.getInvestorStatus().equals(InvestorStatus.ACCEPTED)) {
            ProjectInbox projectInbox = ProjectInbox.builder()
                    .projectId(project.getId())
                    .projectName(project.getName())
//                    .userId(developer.getUserId())
//                    .developerId(project.getDeveloperId())
                    .investorId(project.getInvestorId())
                    .investorUserId(userCurrent.getId())
                    .investorName(investor.getCompanyName())
                    .createdDate(LocalDateTime.now())
                    .messages( userCurrent.getFirstName() +" "+ userCurrent.getLastName()+ "  has accepted  " + project.getName())
                    .build();
            projectInboxRepository.save(projectInbox);
        }

        if(request.getInvestorStatus().equals(InvestorStatus.DECLINED)) {
            ProjectInbox projectInbox = ProjectInbox.builder()
                    .projectId(project.getId())
                    .projectName(project.getName())
//                    .userId(developer.getUserId())
//                    .developerId(project.getDeveloperId())
                    .investorId(project.getInvestorId())
                    .investorUserId(userCurrent.getId())
                    .investorName(investor.getCompanyName())
                    .createdDate(LocalDateTime.now())
                    .messages( userCurrent.getFirstName() +" "+ userCurrent.getLastName()+ "  has declined  " + project.getName())
                    .build();
            projectInboxRepository.save(projectInbox);
        }
        return mapper.map(project, ProjectResponse.class);
    }




    public void sendToAdmins(Project projectResponse)  {

        List<User> sendToAdmin = userRepository.findByUserCategory(ADMIN);
        for (User tran : sendToAdmin
        ) {

            ProjectInbox projectInbox= ProjectInbox.builder()
                    .projectName(projectResponse.getName())
                    .adminId(tran.getId())
                    .projectId(projectResponse.getId())
                    .developerId(projectResponse.getDeveloperId())
                    .createdDate(LocalDateTime.now())
                    .messages("A new project has been created")
                    .build();
            projectInboxRepository.save(projectInbox);
            log.info("::::: message saved successfully :: ");
        }
    }

}
