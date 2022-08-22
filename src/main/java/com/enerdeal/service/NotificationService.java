package com.enerdeal.service;


import com.enerdeal.helper.API;
import com.enerdeal.notification.requestDto.NotificationRequestDto;
import com.enerdeal.notification.requestDto.RecipientRequest;
import com.enerdeal.notification.requestDto.SmsRequest;
import com.enerdeal.notification.requestDto.VoiceOtpRequest;
import com.enerdeal.notification.responseDto.NotificationResponseDto;
import com.enerdeal.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("ALL")
@Slf4j
@Service
public class NotificationService {



    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.sender}")
    private String mailSender;

    @Value("${mail.hostName}")
    private String mailHostName;

    @Value("${mail.smtpPort}")
    private String mailSmtpPort;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    @Value("${space.sms.url}")
    private String smsNotification;

    @Value("${space.notification.url}")
    private String multipleNotification;

    @Value("${authKey.notification}")
    private String authKey;

    @Value("${phoneNo.notification}")
    private String phoneNo;

    @Value("${notification.unique.id}")
    private String uniqueId;

    @Value("${voice.otp.url}")
    private String voiceOtp;

    @Autowired
    private API api;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ModelMapper mapper;

    public NotificationService(ModelMapper mapper) {
        this.mapper = mapper;
    }


//    public void emailNotificationRequest(NotificationRequestDto notificationRequestDto) {
//
//        ExecutorSingleton.getInstance().execute(() -> {
//            try {
//                List<InternetAddress> recipients = Arrays.asList(new InternetAddress(notificationRequestDto.getMail()));
//                try {
//
//                    Email email = new SimpleEmail()
//                            .setMsg(notificationRequestDto.getMessage())
//                            .setTo(recipients);
//
//                    email.setFrom(mailFrom, mailSender);
//                    email.setSubject(notificationRequestDto.getTitle());
//                    email.setHostName(mailHostName);
//                    email.setSSL(true);
//                    email.setSSLOnConnect(true);
//                    //email.setSmtpPort(config.getInt("mail.smtpPort"));
//                    email.setStartTLSEnabled(false);
//                    email.setStartTLSRequired(false);
//                    email.setDebug(true);
//                    email.setSSLCheckServerIdentity(false);
//                    email.setSslSmtpPort(mailSmtpPort);
//                    email.setAuthentication(mailUsername, mailPassword);
//
//                    email.send();
//                } catch (EmailException e) {
//                    logger.error("Error sending login credentials via email.", e);
//                }
//            } catch (AddressException e) {
//                logger.error("Error", e);
//            }
//        });
//
//    }

    public NotificationResponseDto emailNotificationRequest (NotificationRequestDto notificationRequestDto){
        Map<String,String> map = new HashMap();
        map.put("auth-key", authKey);
        map.put("fingerprint", uniqueId);

        notificationRequestDto.setEmail(true);
        notificationRequestDto.setInApp(true);
        notificationRequestDto.setMessage(notificationRequestDto.getMessage());
        notificationRequestDto.getRecipient().forEach(p -> {
            RecipientRequest tran = RecipientRequest.builder()
                    .email(p.getEmail())
                    .build();
            p.setPhoneNo(phoneNo);
        });
        notificationRequestDto.setSms(false);
        notificationRequestDto.setTitle(Constants.NOTIFICATION);
        NotificationResponseDto response = api.post(multipleNotification, notificationRequestDto, NotificationResponseDto.class, map);
        return response;

    }

    public String smsNotificationRequest (SmsRequest smsRequest){

        Map<String,String> map = new HashMap();
        map.put("fingerprint", uniqueId);

        String response = api.post(smsNotification, smsRequest, String.class, map);
        return response;

    }

    public String voiceOtp (VoiceOtpRequest voiceOtpRequest){
        Map<String,String> map = new HashMap();
        map.put("fingerprint", uniqueId);
        String response = api.post(voiceOtp, voiceOtpRequest, String.class, map);
        return response;
    }

}
