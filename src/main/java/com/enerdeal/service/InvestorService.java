package com.enerdeal.service;

import com.enerdeal.dto.requestDto.ChangePasswordDto;
import com.enerdeal.dto.requestDto.EnableDisableDto;
import com.enerdeal.dto.requestDto.InvestorRequest;
import com.enerdeal.dto.requestDto.InvestorSignUpRequest;
import com.enerdeal.dto.responseDto.InvestorActivationResponse;
import com.enerdeal.dto.responseDto.InvestorResponse;
import com.enerdeal.dto.responseDto.InvestorSignUpResponse;
import com.enerdeal.enums.UserCategory;
import com.enerdeal.exceptions.BadRequestException;
import com.enerdeal.exceptions.ConflictException;
import com.enerdeal.exceptions.NotFoundException;
import com.enerdeal.helper.Validations;
import com.enerdeal.model.Investor;
import com.enerdeal.model.PreviousPasswords;
import com.enerdeal.model.User;
import com.enerdeal.notification.requestDto.NotificationRequestDto;
import com.enerdeal.notification.requestDto.RecipientRequest;
import com.enerdeal.repo.InvestorRepository;
import com.enerdeal.repo.PreviousPasswordRepository;
import com.enerdeal.repo.UserRepository;
import com.enerdeal.utils.CustomResponseCode;
import com.enerdeal.utils.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InvestorService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private InvestorRepository repository;
    private UserRepository userRepository;
    private PreviousPasswordRepository previousPasswordRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;
    private NotificationService notificationService;


    public InvestorService(InvestorRepository repository, UserRepository userRepository,
                            PreviousPasswordRepository previousPasswordRepository, ModelMapper mapper,
                            ObjectMapper objectMapper, Validations validations, NotificationService notificationService){
        this.repository = repository;
        this.userRepository = userRepository;
        this.previousPasswordRepository = previousPasswordRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
        this.notificationService = notificationService;
    }

    public InvestorSignUpResponse InvestorSignUp(InvestorSignUpRequest request, HttpServletRequest request1) {
        validations.validateInvestor(request);
        User user = mapper.map(request,User.class);

        User exist = userRepository.findByEmailOrPhone(request.getEmail(),request.getPhone());
        if(exist !=null && exist.getPasswordChangedOn()== null){

            Investor investorExist = repository.findByUserId(exist.getId());
            if(investorExist !=null){
                InvestorSignUpResponse InvestorSignUpResponseDto= InvestorSignUpResponse.builder()
                        .id(exist.getId())
                        .email(exist.getEmail())
                        .firstName(exist.getFirstName())
                        .lastName(exist.getLastName())
                        .phone(exist.getPhone())
                        .username(exist.getUsername())
                        .investorId(investorExist.getId())
                        .build();
                return InvestorSignUpResponseDto;
            }else {
                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, " Investor id does not exist");
            }

        }else if(exist !=null && exist.getPasswordChangedOn() !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Investor user already exist");
        }
        String password = request.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setUserCategory(UserCategory.INVESTOR);
        user.setUsername(request.getEmail());
        user.setLoginAttempts(0);
        user.setResetToken(Utility.registrationCode("HHmmss"));
        user.setResetTokenExpirationDate(Utility.tokenExpiration());
        user.setCreatedBy(0l);
        user.setIsActive(false);
        user = userRepository.save(user);
        log.debug("Create new Investor - {}"+ new Gson().toJson(user));

        PreviousPasswords previousPasswords = PreviousPasswords.builder()
                .userId(user.getId())
                .password(user.getPassword())
                .createdDate(LocalDateTime.now())
                .build();
        previousPasswordRepository.save(previousPasswords);

        Investor saveInvestor = new Investor();
        saveInvestor.setUserId(user.getId());
        saveInvestor.setIsActive(false);
        saveInvestor.setCreatedBy(user.getId());
        saveInvestor.setCompanyName(request.getCompanyName());
        saveInvestor.setEmail(request.getEmail());
        saveInvestor.setPhone(request.getPhone());
        saveInvestor.setAddress(request.getAddress());

        Investor investorResponse= repository.save(saveInvestor);
        log.debug("Create new Investor  - {}"+ new Gson().toJson(saveInvestor));


        InvestorSignUpResponse response = InvestorSignUpResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .companyName(investorResponse.getCompanyName())
                .investorId(investorResponse.getId())
                .build();

        // --------  sending token  -----------

        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        User emailRecipient = userRepository.getOne(user.getId());
        notificationRequestDto.setMessage("Activation Otp " + " " + user.getResetToken());
        List<RecipientRequest> recipient = new ArrayList<>();
        recipient.add(RecipientRequest.builder()
                .email(emailRecipient.getEmail())
                .build());
        notificationRequestDto.setRecipient(recipient);
        notificationRequestDto.setMail(emailRecipient.getEmail());
        notificationService.emailNotificationRequest(notificationRequestDto);

        return response;
    }

    public InvestorActivationResponse InvestorPasswordActivation(ChangePasswordDto request) {

        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested user id does not exist!"));
        mapper.map(request, user);

        String password = request.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordChangedOn(LocalDateTime.now());
        user = userRepository.save(user);

        PreviousPasswords previousPasswords = PreviousPasswords.builder()
                .userId(user.getId())
                .password(user.getPassword())
                .createdDate(LocalDateTime.now())
                .build();
        previousPasswordRepository.save(previousPasswords);

        Investor Investor = repository.findByUserId(user.getId());

        InvestorActivationResponse response = InvestorActivationResponse.builder()
                .userId(user.getId())
                .investorId(Investor.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();

        return response;
    }

    public InvestorResponse createInvestorProperties(InvestorRequest request) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Investor investorProperties = mapper.map(request,Investor.class);
        Investor exist = repository.findInvestorById(request.getId());
        if(exist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Investor properties already exist");
        }
        investorProperties.setCreatedBy(userCurrent.getId());
        investorProperties.setIsActive(true);
        investorProperties = repository.save(investorProperties);
        log.debug("Create new Investor properties - {}"+ new Gson().toJson(investorProperties));
        return mapper.map(investorProperties, InvestorResponse.class);
    }


    public InvestorResponse updateInvestorProperties(InvestorRequest request,HttpServletRequest request1) {
        validations.validateInvestorUpdate(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Investor investorProperties = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Investor properties Id does not exist!"));
        mapper.map(request, investorProperties);
        investorProperties.setUpdatedBy(userCurrent.getId());
        repository.save(investorProperties);
        log.debug("Investor asset record updated - {}"+ new Gson().toJson(investorProperties));

        return mapper.map(investorProperties, InvestorResponse.class);
    }


    public InvestorResponse findInvestorAsset(Long id){
        Investor InvestorProperties  = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Investor properties Id does not exist!"));
        return mapper.map(InvestorProperties,InvestorResponse.class);
    }


    public Page<Investor> findAll(String name, PageRequest pageRequest ){
        Page<Investor> InvestorProperties = repository.findInvestorsProperties(name,pageRequest);
        if(InvestorProperties == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }

        return InvestorProperties;

    }



    public void enableDisEnable (EnableDisableDto request, HttpServletRequest request1){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Investor investorProperties = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Investor properties Id does not exist!"));
        investorProperties.setIsActive(request.getIsActive());
        investorProperties.setUpdatedBy(userCurrent.getId());
        repository.save(investorProperties);

    }


    public List<Investor> getAll(Boolean isActive){
        List<Investor> investorProperties = repository.findByIsActive(isActive);
        return investorProperties;

    }
}
