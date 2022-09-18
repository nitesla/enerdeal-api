package com.enerdeal.service;

import com.enerdeal.dto.requestDto.*;
import com.enerdeal.dto.responseDto.ActivateUserResponse;
import com.enerdeal.dto.responseDto.AdminActivationResponse;
import com.enerdeal.dto.responseDto.UserResponse;
import com.enerdeal.enums.UserCategory;
import com.enerdeal.exceptions.BadRequestException;
import com.enerdeal.exceptions.ConflictException;
import com.enerdeal.exceptions.NotFoundException;
import com.enerdeal.helper.CoreValidations;
import com.enerdeal.model.Developer;
import com.enerdeal.model.Investor;
import com.enerdeal.model.PreviousPasswords;
import com.enerdeal.model.User;
import com.enerdeal.notification.requestDto.NotificationRequestDto;
import com.enerdeal.notification.requestDto.RecipientRequest;
import com.enerdeal.repo.DeveloperRepository;
import com.enerdeal.repo.InvestorRepository;
import com.enerdeal.repo.PreviousPasswordRepository;
import com.enerdeal.repo.UserRepository;
import com.enerdeal.utils.CustomResponseCode;
import com.enerdeal.utils.Utility;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@SuppressWarnings("ALL")
@Slf4j
@Service
public class UserService {
    @Value("${token.time.to.leave}")
    long tokenTimeToLeave;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WhatsAppService whatsAppService;

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private DeveloperRepository developerRepository;

    private PreviousPasswordRepository previousPasswordRepository;
    private UserRepository userRepository;
    private NotificationService notificationService;
    private final ModelMapper mapper;
    private final CoreValidations coreValidations;


    public UserService(PreviousPasswordRepository previousPasswordRepository, UserRepository userRepository,
                       NotificationService notificationService,
                       ModelMapper mapper, CoreValidations coreValidations) {
        this.previousPasswordRepository = previousPasswordRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.mapper = mapper;
        this.coreValidations = coreValidations;

    }



    /** <summary>
     * User creation
     * </summary>
     * <remarks>this method is responsible for creation of new user</remarks>
     */

    public UserResponse createAdminUser(AdminUserSignUpDto request,HttpServletRequest request1) {
        coreValidations.validateUser(request);
        User userExist = userRepository.findByEmailOrPhone(request.getEmail(),request.getPhone());
        if(userExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Admin User already exist");
        }
//        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        User user = mapper.map(request,User.class);
        String password = request.getPassword();
//        String password = Utility.getSaltString();
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(request.getEmail());
        user.setCreatedBy(0l);
        user.setUserCategory(UserCategory.ADMIN);
        user.setIsActive(false);
        user.setLoginAttempts(0);
        user.setResetToken(Utility.registrationCode("HHmmss"));
        user.setResetTokenExpirationDate(Utility.tokenExpiration());
        user = userRepository.save(user);
        log.debug("Create new user - {}"+ new Gson().toJson(user));


        PreviousPasswords previousPasswords = PreviousPasswords.builder()
                .userId(user.getId())
                .password(user.getPassword())
                .createdDate(LocalDateTime.now())
                .build();
        previousPasswordRepository.save(previousPasswords);

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
//                .message("Enerdeal Account Activation Otp:  " + " " + user.getResetToken())
//                .phoneNumber(emailRecipient.getPhone())
//                .build();
//        whatsAppService.whatsAppNotification(whatsAppRequest);

//        VoiceOtpRequest voiceOtpRequest = VoiceOtpRequest.builder()
//                .message("Enerdeal Account Activation Otp: " + " " + user.getResetToken())
//                .phoneNumber(emailRecipient.getPhone())
//                .build();
//        notificationService.voiceOtp(voiceOtpRequest);

        return mapper.map(user, UserResponse.class);
    }



    /** <summary>
     * User update
     * </summary>
     * <remarks>this method is responsible for updating already existing user</remarks>
     */

    public UserResponse updateUser(UserDto request,HttpServletRequest request1) {
        coreValidations.updateUser(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested user id does not exist!"));
        mapper.map(request, user);
        user.setUpdatedBy(userCurrent.getId());
        userRepository.save(user);
        log.debug("user record updated - {}"+ new Gson().toJson(user));


        return mapper.map(user, UserResponse.class);
    }



    /** <summary>
     * Find user
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public UserResponse findUser(Long id){
        User user  = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested user id does not exist!"));

       UserResponse userResponse = UserResponse.builder()
               .createdBy(user.getCreatedBy())
               .createdDate(user.getCreatedDate())
               .email(user.getEmail())
               .failedLoginDate(user.getFailedLoginDate())
               .firstName(user.getFirstName())
               .id(user.getId())
               .isActive(user.getIsActive())
               .lastLogin(user.getLastLogin())
               .lastName(user.getLastName())
               .middleName(user.getMiddleName())
               .phone(user.getPhone())
               .updatedBy(user.getUpdatedBy())
               .updatedDate(user.getUpdatedDate())
               .build();


        return userResponse;
    }



    /** <summary>
     * Find all users
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<User> findAll(String firstName,String lastName,String phone,Boolean isActive,String email, PageRequest pageRequest ){
        Page<User> users = userRepository.findUsers(firstName,lastName,phone,isActive,email,pageRequest);
        if(users == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return users;

    }







    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a user</remarks>
     */
    public void enableDisEnableUser (EnableDisableDto request,HttpServletRequest request1){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        User user  = userRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested user id does not exist!"));
        user.setIsActive(request.getIsActive());
        user.setUpdatedBy(userCurrent.getId());

    }



    /** <summary>
     * Change password
     * </summary>
     * <remarks>this method is responsible for changing password</remarks>
     */

    public void changeUserPassword(ChangePasswordDto request) {
        coreValidations.changePassword(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested user id does not exist!"));
        mapper.map(request, user);
            if(getPrevPasswords(user.getId(),request.getPassword())){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Password already used");
        }
        if (!getPrevPasswords(user.getId(), request.getPreviousPassword())) {
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid previous password");
        }
        String password = request.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setIsActive(true);
        user.setLockedDate(null);
        user.setUpdatedBy(userCurrent.getId());
        user = userRepository.save(user);

        PreviousPasswords previousPasswords = PreviousPasswords.builder()
                .userId(user.getId())
                .password(user.getPassword())
                .createdDate(LocalDateTime.now())
                .build();
        previousPasswordRepository.save(previousPasswords);

    }


    /** <summary>
     * Previous password
     * </summary>
     * <remarks>this method is responsible for fetching the last 4 passwords</remarks>
     */

    public Boolean getPrevPasswords(Long userId,String password){
        List<PreviousPasswords> prev = previousPasswordRepository.previousPasswords(userId);
        for (PreviousPasswords pass : prev
                ) {
            if (passwordEncoder.matches(password, pass.getPassword())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }





    /** <summary>
     * Unlock account
     * </summary>
     * <remarks>this method is responsible for unlocking a user account</remarks>
     */
    public void unlockAccounts (UnlockAccountRequestDto request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested user id does not exist!"));
        mapper.map(request, user);
        user.setLockedDate(null);
        user.setLoginAttempts(0);
        userRepository.save(user);

    }



    /** <summary>
     * Lock account
     * </summary>
     * <remarks>this method is responsible for lock a user account</remarks>
     */
    public void lockLogin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested user id does not exist!"));
        user.setLockedDate(new Date());
        userRepository.save(user);
    }


    /** <summary>
     * Forget password
     * </summary>
     * <remarks>this method is responsible for a user that forgets his password</remarks>
     */

    public void forgetPassword (ForgetPasswordDto request) {

        if(request.getEmail() != null) {

            User user = userRepository.findByEmail(request.getEmail());
            if (user == null) {
                throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Invalid email");
            }
            if (user.getIsActive() == false) {
                throw new BadRequestException(CustomResponseCode.FAILED, "User account has been disabled");
            }
            user.setResetToken(Utility.registrationCode("HHmmss"));
            user.setResetTokenExpirationDate(Utility.tokenExpiration());
            userRepository.save(user);

            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            User emailRecipient = userRepository.getOne(user.getId());
            notificationRequestDto.setMessage("Enerdeal Account Activation Otp:  " + " " + user.getResetToken());
            List<RecipientRequest> recipient = new ArrayList<>();
            recipient.add(RecipientRequest.builder()
                    .email(emailRecipient.getEmail())
                    .build());
            notificationRequestDto.setRecipient(recipient);
            notificationRequestDto.setMail(emailRecipient.getEmail());
            notificationService.emailNotificationRequest(notificationRequestDto);


//            SmsRequest smsRequest = SmsRequest.builder()
//                    .message("Enerdeal Account Activation Otp:  "+ " " + user.getResetToken())
//                    .phoneNumber(emailRecipient.getPhone())
//                    .build();
//            notificationService.smsNotificationRequest(smsRequest);

//            WhatsAppRequest whatsAppRequest = WhatsAppRequest.builder()
//                    .message("Enerdeal Account Activation Otp:  " + " " + user.getResetToken())
//                    .phoneNumber(emailRecipient.getPhone())
//                    .build();
//            whatsAppService.whatsAppNotification(whatsAppRequest);

//            VoiceOtpRequest voiceOtpRequest = VoiceOtpRequest.builder()
//                    .message("Enerdeal Account Activation Otp:  " + " " + user.getResetToken())
//                    .phoneNumber(emailRecipient.getPhone())
//                    .build();
//            notificationService.voiceOtp(voiceOtpRequest);

        }else if(request.getPhone()!= null) {

            User userPhone = userRepository.findByPhone(request.getPhone());
            if (userPhone == null) {
                throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Invalid phone number");
            }
            if (userPhone.getIsActive() == false) {
                throw new BadRequestException(CustomResponseCode.FAILED, "User account has been disabled");
            }
            userPhone.setResetToken(Utility.registrationCode("HHmmss"));
            userPhone.setResetTokenExpirationDate(Utility.tokenExpiration());
            userRepository.save(userPhone);


            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            User emailRecipient = userRepository.getOne(userPhone.getId());
            notificationRequestDto.setMessage("Activation Otp " + " " + userPhone.getResetToken());
            List<RecipientRequest> recipient = new ArrayList<>();
            recipient.add(RecipientRequest.builder()
                    .email(emailRecipient.getEmail())
                    .build());
            notificationRequestDto.setRecipient(recipient);
            notificationRequestDto.setMail(emailRecipient.getEmail());
            notificationService.emailNotificationRequest(notificationRequestDto);
//            SmsRequest smsRequest = SmsRequest.builder()
//                    .message("Activation Otp " + " " + userPhone.getResetToken())
//                    .phoneNumber(emailRecipient.getPhone())
//                    .build();
//            notificationService.smsNotificationRequest(smsRequest);

//            WhatsAppRequest whatsAppRequest = WhatsAppRequest.builder()
//                    .message("Activation Otp " + " " + userPhone.getResetToken())
//                    .phoneNumber(emailRecipient.getPhone())
//                    .build();
//            whatsAppService.whatsAppNotification(whatsAppRequest);

//            VoiceOtpRequest voiceOtpRequest = VoiceOtpRequest.builder()
//                    .message("Activation Otp is " + " " + userPhone.getResetToken())
//                    .phoneNumber(emailRecipient.getPhone())
//                    .build();
//            notificationService.voiceOtp(voiceOtpRequest);
        }

    }

    /** <summary>
     * Activate user
     * </summary>
     * <remarks>this method is responsible for activating users</remarks>
     */

    public ActivateUserResponse activateUser (ActivateUserAccountDto request) {
        User user = userRepository.findByResetToken(request.getResetToken());
        if(user == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Invalid OTP supplied");
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calobj = Calendar.getInstance();
        String currentDate = df.format(calobj.getTime());
        String regDate = user.getResetTokenExpirationDate();
        String result = String.valueOf(currentDate.compareTo(regDate));
        if(result.equals("1")){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, " OTP invalid/expired");
        }

        request.setUpdatedBy(user.getId());
        request.setIsActive(true);
        request.setPasswordChangedOn(LocalDateTime.now());
        userOTPValidation(user,request);

        Boolean isInvestor = null;
        Investor investor = investorRepository.findInvestorByUserId(user.getId());
        Developer developer = developerRepository.findDeveloperByUserId(user.getId());
        if (investor != null) {
            isInvestor = true;
        }

        if (developer != null) {
            isInvestor = false;
        }


        ActivateUserResponse response = ActivateUserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isInvestor(isInvestor)
                .build();

        return response;

    }



    public User userOTPValidation(User user, ActivateUserAccountDto activateUserAccountDto) {
        user.setUpdatedBy(activateUserAccountDto.getUpdatedBy());
        user.setIsActive(activateUserAccountDto.getIsActive());
        user.setPasswordChangedOn(activateUserAccountDto.getPasswordChangedOn());
        return userRepository.saveAndFlush(user);
    }


    public Boolean matchPasswords(Long id,String password){
        User prev = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested user id does not exist!"));
            if (passwordEncoder.matches(password,prev.getPassword())) {
                return Boolean.TRUE;
            }

        return Boolean.FALSE;
    }



    /** <summary>
     * Change Transaction pin
     * </summary>
     * <remarks>this method is responsible for changing transaction pin </remarks>
     */




    public User loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
            if (null == user) {
                return null;
            } else {

                    if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                        user.setLoginStatus(true);
                    } else {
                        user.setLoginStatus(false);
                    }
                return user;
            }

    }


    public List<User> getAll(Boolean isActive){
        List<User> user = userRepository.findByIsActive(isActive);
        return user;

    }



    public void updateFailedLogin(Long id){
        User userExist = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " user id does not exist!"));
        if(userExist != null){
            userExist.setFailedLoginDate(LocalDateTime.now());

            int count = increment(userExist.getLoginAttempts());
            userExist.setLoginAttempts(count);
            userRepository.save(userExist);

        }
    }




    public static int increment(int number){
        // Declaring the number
        // Converting the number to String
        String string_num = Integer.toString(number);

        // Finding the length of the number
        int len = string_num.length();

        // Declaring the empty string
        String add = "";

        // Generating the addition string
        for (int i = 0; i < len; i++) {
            add = add.concat("1");
        }

        // COnverting it to Integer
        int str_num = Integer.parseInt(add);

        // Adding them and displaying the result
        System.out.println(number + str_num);

        return number + str_num;
    }






    public void updateLogin(Long id){
        User userExist = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " user id does not exist!"));
        if(userExist != null){
            userExist.setLockedDate(null);
            userExist.setLoginAttempts(0);
            userExist.setLastLogin(LocalDateTime.now());
            userRepository.save(userExist);

        }
    }





    public long getSessionExpiry() {
        //TODO Token expiry in seconds: 900 = 15mins
        return tokenTimeToLeave / 60;
    }

    public AdminActivationResponse adminPasswordActivation(ChangePasswordDto request) {

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

        AdminActivationResponse response = AdminActivationResponse.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();

        return response;
    }

    public void resendOTP(ResendOtpDto request) {
        coreValidations.validateResendOTP(request);
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Invalid email");
        }
        user.setResetToken(Utility.registrationCode("HHmmss"));
        user.setResetTokenExpirationDate(Utility.tokenExpiration());
        userRepository.save(user);

        String msg = "Hello " + " " + user.getFirstName() + " " + user.getLastName()
                + "     Username :" + " "+ user.getUsername()
                + "     Activation OTP :" + " "+ user.getResetToken() ;

        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        User emailRecipient = userRepository.getOne(user.getId());
        notificationRequestDto.setMessage(msg);
        List<RecipientRequest> recipient = new ArrayList<>();
        recipient.add(RecipientRequest.builder()
                .email(emailRecipient.getEmail())
                .build());
        notificationRequestDto.setRecipient(recipient);
        notificationRequestDto.setMail(emailRecipient.getEmail());
        notificationService.emailNotificationRequest(notificationRequestDto);

//        SmsRequest smsRequest = SmsRequest.builder()
//                .message(msg)
//                .phoneNumber(emailRecipient.getPhone())
//                .build();
//        notificationService.smsNotificationRequest(smsRequest);

//        WhatsAppRequest whatsAppRequest = WhatsAppRequest.builder()
//                .message(msg)
//                .phoneNumber(emailRecipient.getPhone())
//                .build();
//        whatsAppService.whatsAppNotification(whatsAppRequest);
    }

}
