package com.enerdeal.service;

import com.enerdeal.dto.requestDto.ChangePasswordDto;
import com.enerdeal.dto.requestDto.DeveloperDto;
import com.enerdeal.dto.requestDto.DeveloperSignUpDto;
import com.enerdeal.dto.requestDto.EnableDisableDto;
import com.enerdeal.dto.responseDto.DeveloperActivationResponse;
import com.enerdeal.dto.responseDto.DeveloperResponseDto;
import com.enerdeal.dto.responseDto.DeveloperSignUpResponseDto;
import com.enerdeal.enums.UserCategory;
import com.enerdeal.exceptions.BadRequestException;
import com.enerdeal.exceptions.ConflictException;
import com.enerdeal.exceptions.NotFoundException;
import com.enerdeal.helper.Validations;
import com.enerdeal.model.Developer;
import com.enerdeal.model.PreviousPasswords;
import com.enerdeal.model.User;
import com.enerdeal.notification.requestDto.NotificationRequestDto;
import com.enerdeal.notification.requestDto.RecipientRequest;
import com.enerdeal.repo.DeveloperRepository;
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


@SuppressWarnings("ALL")
@Slf4j
@Service
public class DeveloperService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WhatsAppService whatsAppService;
    private DeveloperRepository repository;
    private UserRepository userRepository;
    private PreviousPasswordRepository previousPasswordRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;
    private NotificationService notificationService;


    public DeveloperService(DeveloperRepository repository, UserRepository userRepository,
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

    public DeveloperSignUpResponseDto developerSignUp(DeveloperSignUpDto request, HttpServletRequest request1) {
        validations.validateDeveloper(request);
        User user = mapper.map(request,User.class);

        User exist = userRepository.findByEmailOrPhone(request.getEmail(),request.getPhone());
        if(exist !=null && exist.getPasswordChangedOn()== null){

            Developer developerExist = repository.findByUserId(exist.getId());
            if(developerExist !=null){
                DeveloperSignUpResponseDto developerSignUpResponseDto= DeveloperSignUpResponseDto.builder()
                  .id(exist.getId())
                  .email(exist.getEmail())
                  .firstName(exist.getFirstName())
                  .lastName(exist.getLastName())
                  .phone(exist.getPhone())
                  .username(exist.getUsername())
                  .developerId(developerExist.getId())
                  .build();
          return developerSignUpResponseDto;
            }else {
                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, " Developer id does not exist");
            }

        }else if(exist !=null && exist.getPasswordChangedOn() !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Developer user already exist");
        }
        String password = request.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setUserCategory(UserCategory.DEVELOPER);
        user.setUsername(request.getEmail());
        user.setLoginAttempts(0);
        user.setResetToken(Utility.registrationCode("HHmmss"));
        user.setResetTokenExpirationDate(Utility.tokenExpiration());
        user.setCreatedBy(0l);
        user.setIsActive(false);
        user = userRepository.save(user);
        log.debug("Create new developer - {}"+ new Gson().toJson(user));

        PreviousPasswords previousPasswords = PreviousPasswords.builder()
                .userId(user.getId())
                .password(user.getPassword())
                .createdDate(LocalDateTime.now())
                .build();
        previousPasswordRepository.save(previousPasswords);

        Developer saveDeveloper = new Developer();
        saveDeveloper.setUserId(user.getId());
        saveDeveloper.setIsActive(false);
        saveDeveloper.setCreatedBy(user.getId());
        saveDeveloper.setCompanyName(request.getCompanyName());

        Developer developerResponse= repository.save(saveDeveloper);
        log.debug("Create new Developer  - {}"+ new Gson().toJson(saveDeveloper));


        DeveloperSignUpResponseDto response = DeveloperSignUpResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .companyName(developerResponse.getCompanyName())
                .developerId(developerResponse.getId())
                .build();

        // --------  sending token  -----------

        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        User emailRecipient = userRepository.getOne(user.getId());
        notificationRequestDto.setMessage("Enerdeal Account Activation Otp: " + " " + user.getResetToken());
        List<RecipientRequest> recipient = new ArrayList<>();
        recipient.add(RecipientRequest.builder()
                .email(emailRecipient.getEmail())
                .build());
        notificationRequestDto.setRecipient(recipient);
        notificationRequestDto.setMail(emailRecipient.getEmail());
        notificationService.emailNotificationRequest(notificationRequestDto);

//        SmsRequest smsRequest = SmsRequest.builder()
//                .message("Enerdeal Account Activation Otp: " + " " + user.getResetToken())
//                .phoneNumber(emailRecipient.getPhone())
//                .build();
//        notificationService.smsNotificationRequest(smsRequest);

//        WhatsAppRequest whatsAppRequest = WhatsAppRequest.builder()
//                .message("Enerdeal Account Activation Otp: " + " " + user.getResetToken())
//                .phoneNumber(emailRecipient.getPhone())
//                .build();
//        whatsAppService.whatsAppNotification(whatsAppRequest);

//        VoiceOtpRequest voiceOtpRequest = VoiceOtpRequest.builder()
//                .message("Enerdeal Account Activation Otp: " + " " + user.getResetToken())
//                .phoneNumber(emailRecipient.getPhone())
//                .build();
//        notificationService.voiceOtp(voiceOtpRequest);

        return response;
    }

    public DeveloperActivationResponse developerPasswordActivation(ChangePasswordDto request) {

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

        Developer developer = repository.findByUserId(user.getId());

        DeveloperActivationResponse response = DeveloperActivationResponse.builder()
                .userId(user.getId())
                .developerId(developer.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();

        return response;
    }

    public DeveloperResponseDto createDeveloperProperties(DeveloperDto request) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Developer developerProperties = mapper.map(request,Developer.class);
        Developer exist = repository.findDeveloperById(request.getId());
        if(exist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Developer properties already exist");
        }
        developerProperties.setCreatedBy(userCurrent.getId());
        developerProperties.setIsActive(true);
        developerProperties = repository.save(developerProperties);
        log.debug("Create new developer properties - {}"+ new Gson().toJson(developerProperties));
        return mapper.map(developerProperties, DeveloperResponseDto.class);
    }


    public DeveloperResponseDto updateDeveloperProperties(DeveloperDto request,HttpServletRequest request1) {
        validations.validateDeveloperUpdate(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Developer developerProperties = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested developer properties Id does not exist!"));
        mapper.map(request, developerProperties);
        developerProperties.setUpdatedBy(userCurrent.getId());
        repository.save(developerProperties);
        log.debug("developer asset record updated - {}"+ new Gson().toJson(developerProperties));

        return mapper.map(developerProperties, DeveloperResponseDto.class);
    }


    public DeveloperResponseDto findDeveloperAsset(Long id){
        Developer developerProperties  = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested developer properties Id does not exist!"));
        return mapper.map(developerProperties,DeveloperResponseDto.class);
    }


    public Page<Developer> findAll(String name, PageRequest pageRequest ){
        Page<Developer> developerProperties = repository.findDevelopersProperties(name,pageRequest);
        if(developerProperties == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }

        return developerProperties;

    }



    public void enableDisEnable (EnableDisableDto request, HttpServletRequest request1){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Developer developerProperties = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested developer properties Id does not exist!"));
        developerProperties.setIsActive(request.getIsActive());
        developerProperties.setUpdatedBy(userCurrent.getId());
        repository.save(developerProperties);

    }


    public List<Developer> getAll(Boolean isActive){
        List<Developer> developerProperties = repository.findByIsActive(isActive);
        return developerProperties;

    }
}
