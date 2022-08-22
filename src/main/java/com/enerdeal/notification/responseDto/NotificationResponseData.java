package com.enerdeal.notification.responseDto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponseData {


    private String description;
    private String body;
    private String title;
    private String summary;

}
