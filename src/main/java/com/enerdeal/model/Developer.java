package com.enerdeal.model;


import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Developer extends CoreEntity {

    @Column(updatable= false)
    private long userId;
    private String rcNumber;
    private String address;
    private String companyName;
    private String companyAddress;
    private String companyEmail;
    private String companyPhone;
    private String companyWebsite;
    private String ceoName;
    private String designation;
    private String phone;
    private String email;
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Developer developer = (Developer) o;
        return getId() != null && Objects.equals(getId(), developer.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
