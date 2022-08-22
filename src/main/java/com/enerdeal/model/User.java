package com.enerdeal.model;


import com.enerdeal.enums.UserCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Date;


@EqualsAndHashCode(callSuper=false)
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends CoreEntity{


//    private Long loginAttempts;
    private int loginAttempts;
    private LocalDateTime failedLoginDate;
    private LocalDateTime lastLogin;
    private String password;
    private String passwordExpiration;
    private Date lockedDate;
    private String firstName;
    private String lastName;
    private String middleName;
    private String username;
    private LocalDateTime passwordChangedOn;
    @Transient
    private boolean loginStatus;
    private String email;
    private String phone;
    @Enumerated(value = EnumType.STRING)
    private UserCategory userCategory;
    private String resetToken;
    private String resetTokenExpirationDate;

    private String address;


    @Transient
    private boolean accountNonLocked;
    @Transient
    private String registrationToken;
    @Transient
    private String registrationTokenExpiration;
    @Transient
    private Boolean isEmailVerified ;


}
