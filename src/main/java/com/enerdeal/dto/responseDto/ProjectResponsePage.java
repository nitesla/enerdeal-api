package com.enerdeal.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResponsePage {
    private List<ProjectResponse> projectResponse;
    private long totalElements;
    private long totalPages;
    private long number;
    private long numberOfElements;
}
