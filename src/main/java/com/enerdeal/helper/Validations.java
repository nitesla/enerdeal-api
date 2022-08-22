package com.enerdeal.helper;

import com.enerdeal.dto.requestDto.*;
import com.enerdeal.exceptions.BadRequestException;
import com.enerdeal.exceptions.NotFoundException;
import com.enerdeal.repo.DeveloperRepository;
import com.enerdeal.repo.InvestorRepository;
import com.enerdeal.repo.ProjectRepository;
import com.enerdeal.repo.UserRepository;
import com.enerdeal.utils.CustomResponseCode;
import com.enerdeal.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@SuppressWarnings("All")
@Slf4j
@Service
public class Validations {

   
    private UserRepository userRepository;

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private InvestorRepository investorRepository;


    public Validations(UserRepository userRepository) {
       
        this.userRepository = userRepository;
    }

    public String generateReferenceNumber(int numOfDigits) {
        if (numOfDigits < 1) {
            throw new IllegalArgumentException(numOfDigits + ": Number must be equal or greater than 1");
        }
        long random = (long) Math.floor(Math.random() * 9 * (long) Math.pow(10, numOfDigits - 1)) + (long) Math.pow(10, numOfDigits - 1);
        return Long.toString(random);
    }

    public String generateCode(String code) {
        String encodedString = Base64.getEncoder().encodeToString(code.getBytes());
        return encodedString;
    }

    public void validateDeveloper(DeveloperSignUpDto developer){
        if (developer.getFirstName() == null || developer.getFirstName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "First name cannot be empty");
        if (developer.getFirstName().length() < 2 || developer.getFirstName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid first name  length");
        if (developer.getLastName() == null || developer.getLastName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Last name cannot be empty");
        if (developer.getLastName().length() < 2 || developer.getLastName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid last name  length");

        if (developer.getEmail() == null || developer.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "email cannot be empty");
        if (!Utility.validEmail(developer.getEmail().trim()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid Email Address");
        if (developer.getPhone() == null || developer.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone number cannot be empty");
        if (developer.getPhone().length() < 8 || developer.getPhone().length() > 14)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid phone number  length");
        if (!Utility.isNumeric(developer.getPhone()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for phone number ");
        if (developer.getCompanyName() == null || developer.getCompanyName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");

    }



    public void validateDeveloperUpdate(DeveloperDto developerPropertiesDto) {
        if (developerPropertiesDto.getCompanyName() == null || developerPropertiesDto.getCompanyName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");

        if (developerPropertiesDto.getAddress() == null || developerPropertiesDto.getAddress().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Address cannot be empty");

        if (developerPropertiesDto.getPhone() == null || developerPropertiesDto.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone cannot be empty");
        if (developerPropertiesDto.getEmail() == null || developerPropertiesDto.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Email cannot be empty");
    }


    public void validateProject(ProjectRequest request) {
        developerRepository.findById(request.getDeveloperId()).orElseThrow(() ->
                new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Developer Id not found"));
    }

    public void validateInvestor(InvestorSignUpRequest request) {

    }

    public void validateInvestorUpdate(InvestorRequest request) {
    }

    public void validateProjectId(Long projectId) {
        projectRepository.findById(projectId).orElseThrow(() ->
                new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Project Id not found"));
    }

    public void validateAssignInvestor(AssignInvestor request) {
        projectRepository.findById(request.getProjectId()).orElseThrow(() ->
                new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Project Id not found"));

        investorRepository.findById(request.getInvestorId()).orElseThrow(() ->
                new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Investor Id not found"));
    }

    public void validateInvestorStatus(InvestorStatusRequest request) {
        projectRepository.findById(request.getProjectId()).orElseThrow(() ->
                new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Project Id not found"));

        investorRepository.findById(request.getInvestorId()).orElseThrow(() ->
                new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Investor Id not found"));
    }
}


