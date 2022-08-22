package com.enerdeal.notification.responseDto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;




@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponseDto {


     private String message;
     private List<NotificationData> data;


}
