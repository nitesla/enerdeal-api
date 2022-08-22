package com.enerdeal.helper;


import com.enerdeal.dto.requestDto.*;
import com.enerdeal.exceptions.BadRequestException;
import com.enerdeal.exceptions.ConflictException;
import com.enerdeal.model.User;
import com.enerdeal.repo.UserRepository;
import com.enerdeal.utils.CustomResponseCode;
import com.enerdeal.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@SuppressWarnings("All")
@Slf4j
@Service
public class CoreValidations {
    private UserRepository userRepository;

    public CoreValidations(UserRepository userRepository ){
        this.userRepository = userRepository;
    }


    public void validateUser(AdminUserSignUpDto userDto) {

        if (userDto.getFirstName() == null || userDto.getFirstName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "First name cannot be empty");
        if (!Utility.validateName(userDto.getFirstName()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for First Name ");
        if (userDto.getFirstName().length() < 2 || userDto.getFirstName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid first name  length");

        if (userDto.getLastName() == null || userDto.getLastName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Last name cannot be empty");
        if (!Utility.validateName(userDto.getLastName()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Last Name ");
        if (userDto.getLastName().length() < 2 || userDto.getLastName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid last name  length");


        if (userDto.getEmail() == null || userDto.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "email cannot be empty");
        if (!Utility.validEmail(userDto.getEmail().trim()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid Email Address");
        User user = userRepository.findByEmail(userDto.getEmail());
        if(user !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Email already exist");
        }

        if (userDto.getPhone() == null || userDto.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone number cannot be empty");
        if (userDto.getPhone().length() < 8 || userDto.getPhone().length() > 14)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid phone number  length");
        if (!Utility.isNumeric(userDto.getPhone()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for phone number ");
        User userExist = userRepository.findByPhone(userDto.getPhone());
        if(userExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, "  user phone already exist");
        }
    }


    public void updateUser(UserDto userDto) {


        if (userDto.getFirstName() == null || userDto.getFirstName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "First name cannot be empty");
        if (userDto.getFirstName().length() < 2 || userDto.getFirstName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid first name  length");

        if (userDto.getLastName() == null || userDto.getLastName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Last name cannot be empty");
        if (userDto.getLastName().length() < 2 || userDto.getLastName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid last name  length");

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "email cannot be empty");
        if (!Utility.validEmail(userDto.getEmail().trim()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid Email Address");

        if (userDto.getPhone() == null || userDto.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone number cannot be empty");
        if (userDto.getPhone().length() < 8 || userDto.getPhone().length() > 14)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid phone number  length");
        if (!Utility.isNumeric(userDto.getPhone()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for phone number ");

    }



    public void changePassword(ChangePasswordDto changePasswordDto) {
        if (changePasswordDto.getPassword() == null || changePasswordDto.getPassword().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Password cannot be empty");
        if (changePasswordDto.getPassword().length() < 6 || changePasswordDto.getPassword().length() > 20)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid password length");
        if (changePasswordDto.getPreviousPassword() == null || changePasswordDto.getPreviousPassword().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Previous password cannot be empty");


    }


    public void generatePasswordValidation(GeneratePassword request) {

        if (request.getPhone() == null || request.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone cannot be empty");

    }

    public void validateResendOTP(ResendOtpDto request) {
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!Utility.validEmail(request.getEmail().trim())) {
                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid Email Address");
            }
        } else {
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Email cannot be empty");
        }
    }


}
