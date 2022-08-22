package com.enerdeal.notification.requestDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Notification {

    private Boolean email;
    private Boolean inApp;
    private String message;
    private List<Notify> recipient;
    private Boolean sms;
    private String title;


}
