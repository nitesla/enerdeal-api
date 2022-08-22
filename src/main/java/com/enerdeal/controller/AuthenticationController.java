package com.enerdeal.controller;


import com.enerdeal.config.AuthenticationWithToken;
import com.enerdeal.dto.requestDto.LoginRequest;
import com.enerdeal.dto.responseDto.AccessTokenWithUserDetails;
import com.enerdeal.dto.responseDto.Response;
import com.enerdeal.enums.UserCategory;
import com.enerdeal.exceptions.LockedException;
import com.enerdeal.exceptions.UnauthorizedException;
import com.enerdeal.model.Developer;
import com.enerdeal.model.Investor;
import com.enerdeal.model.User;
import com.enerdeal.repo.DeveloperRepository;
import com.enerdeal.repo.InvestorRepository;
import com.enerdeal.service.TokenService;
import com.enerdeal.service.UserService;
import com.enerdeal.utils.Constants;
import com.enerdeal.utils.CustomResponseCode;
import com.enerdeal.utils.LoggerUtil;
import com.enerdeal.utils.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Slf4j
@SuppressWarnings("All")
@RestController
@RequestMapping(Constants.APP_CONTENT+"authenticate")
public class AuthenticationController {

    @Value("${login.attempts}")
    private int loginAttempts;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private InvestorRepository investorRepository;

    private final UserService userService;


    public AuthenticationController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request) throws JsonProcessingException {

        log.info(":::::::::: login Request %s:::::::::::::" + loginRequest.getUsername());
        String loginStatus;
        String ipAddress = Utility.getClientIp(request);
        User user = userService.loginUser(loginRequest);
        if (user != null) {
            if (user.isLoginStatus()) {
                //FIRST TIME LOGIN
                if (user.getPasswordChangedOn() == null) {
                    Response resp = new Response();
                    resp.setCode(CustomResponseCode.CHANGE_P_REQUIRED);
                    resp.setDescription("Change password Required, account has not been activated");
                    return new ResponseEntity<>(resp, HttpStatus.ACCEPTED);//202
                }
                if (user.getIsActive()==false) {
                    Response resp = new Response();
                    resp.setCode(CustomResponseCode.FAILED);
                    resp.setDescription("User Account Deactivated, please contact Administrator");
                    return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if (user.getLoginAttempts() >= loginAttempts || user.getLockedDate() != null) {
                    // lock account after x failed attempts or locked date is not null
                    userService.lockLogin(user.getId());
                    throw new LockedException(CustomResponseCode.LOCKED_EXCEPTION, "This account has been locked, Kindly contact support");
                }
//                userService.validateGeneratedPassword(user.getId());
            } else {
                //update login failed count and failed login date
                loginStatus = "failed";
                userService.updateFailedLogin(user.getId());
                throw new UnauthorizedException(CustomResponseCode.UNAUTHORIZED, "Invalid Login details.");
            }
        } else {
            //NO NEED TO update login failed count and failed login date SINCE IT DOES NOT EXIST
            throw new UnauthorizedException(CustomResponseCode.UNAUTHORIZED, "Login details does not exist");
        }

        AuthenticationWithToken authWithToken = new AuthenticationWithToken(user, null);
        String newToken = "Bearer" +" "+this.tokenService.generateNewToken();
        authWithToken.setToken(newToken);
        tokenService.store(newToken, authWithToken);
        SecurityContextHolder.getContext().setAuthentication(authWithToken);
        userService.updateLogin(user.getId());

        String developerId = "";
        String investorId = "";
        if (user.getUserCategory().equals(UserCategory.DEVELOPER)) {
            Developer developer = developerRepository.findByUserId(user.getId());
            if(developer !=null){
                developerId = String.valueOf(developer.getId());
            }
        }

        if (user.getUserCategory().equals(UserCategory.INVESTOR)) {
            Investor investor = investorRepository.findByUserId(user.getId());
            if(investor !=null){
                investorId = String.valueOf(investor.getId());
            }
        }

        AccessTokenWithUserDetails details = new AccessTokenWithUserDetails(newToken, user, userService.getSessionExpiry(), developerId, investorId, user.getUserCategory());
        return new ResponseEntity<>(details, HttpStatus.OK);
    }





    @PostMapping("/logout")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean logout() {
        try {
            AuthenticationWithToken auth = (AuthenticationWithToken) SecurityContextHolder.getContext().getAuthentication();
            return tokenService.remove(auth.getToken());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            LoggerUtil.logError(log, ex);
        }
        return false;
    }



}
