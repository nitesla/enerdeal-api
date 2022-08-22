package com.enerdeal.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@ToString
@MappedSuperclass
public abstract class CoreEntity implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(hidden = true)
    private LocalDateTime createdDate = LocalDateTime.now();

    @UpdateTimestamp
    @ApiModelProperty(hidden = true)
    private LocalDateTime updatedDate = LocalDateTime.now();

    private Long createdBy;
    private Long updatedBy;

    private Boolean isActive;


}
