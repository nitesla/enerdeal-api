package com.enerdeal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Investor extends CoreEntity {

    @Column(updatable= false)
    private long userId;
    private String companyName;
    private String designation;
    private String address;
    private String website;
    private String rcNumber;
    private String ceoName;
    private String phone;
    private String email;
    private LocalDateTime registrationDate = LocalDateTime.now();
}
