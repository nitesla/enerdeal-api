package com.enerdeal.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class ProjectDocuments {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    private String projectFileName;
    private String projectfileType;
    @ApiModelProperty(hidden = true)
    private LocalDateTime createdDate = LocalDateTime.now();


    @Transient
    private String fileURL;
}
