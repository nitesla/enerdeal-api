package com.enerdeal.service;


import com.enerdeal.helper.API;
import com.enerdeal.helper.ExecutorSingleton;
import com.enerdeal.notification.requestDto.NotificationRequestDto;
import com.enerdeal.notification.requestDto.RecipientRequest;
import com.enerdeal.notification.requestDto.SmsRequest;
import com.enerdeal.notification.requestDto.VoiceOtpRequest;
import com.enerdeal.notification.responseDto.NotificationResponseDto;
import com.enerdeal.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;


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

    @Value("${mail.subject}")
    private String subject;

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
    static final String CONFIGSET = "ConfigSet";
    @Autowired
    private API api;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ModelMapper mapper;

    public NotificationService(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public void emailNotificationRequest(NotificationRequestDto notificationRequestDto) {
        final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUsername, mailPassword);
            }

        });

        try {
            final Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(notificationRequestDto.getMail()));
            message.setFrom(new InternetAddress(mailFrom, mailSender));
            message.setSubject(subject);
            message.setText(notificationRequestDto.getMessage());
            message.setSentDate(new Date());
            Transport.send(message);
        } catch (final MessagingException ex) {
            logger.info("Error sending email: " + ex.getMessage(), ex);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties getEmailProperties() {
        final Properties config = new Properties();
        config.put("mail.smtp.auth", "true");
        config.put("mail.smtp.starttls.enable", "true");
        config.put("mail.smtp.ssl.protocols", "TLSv1.2");
        config.put("mail.smtp.host", mailHostName);
        config.put("mail.smtp.port", mailSmtpPort);
        return config;
    }
    public void emailNotificationRequestAmazon(NotificationRequestDto notificationRequestDto) {

        try {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", mailSmtpPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        // Create a Session object to represent a mail session with the specified properties.
        Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information.
        MimeMessage msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(mailFrom, mailSender));

        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(notificationRequestDto.getMail()));

        msg.setSubject(notificationRequestDto.getTitle());
        msg.setContent(notificationRequestDto.getMessage(), "text/html");

        // Add a configuration set header. Comment or delete the
        // next line if you are not using a configuration set
        msg.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);

        // Create a transport.
        Transport transport = session.getTransport();

        // Send the message.
            try {
                System.out.println("Sending...");

                // Connect to Amazon SES using the SMTP username and password you specified above.
                transport.connect(mailHostName, mailUsername, mailPassword);

                // Send the email.
                transport.sendMessage(msg, msg.getAllRecipients());
                System.out.println("Email sent!");
            } catch (Exception ex) {
                System.out.println("The email was not sent.");
                System.out.println("Error message: " + ex.getMessage());
            } finally {
                // Close and terminate the connection.
                transport.close();
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void emailNotificationRequestOption2(NotificationRequestDto notificationRequestDto) {

        ExecutorSingleton.getInstance().execute(() -> {
            try {
                List<InternetAddress> recipients = Arrays.asList(new InternetAddress(notificationRequestDto.getMail()));
                try {

                    Email email = new SimpleEmail()
                            .setMsg(notificationRequestDto.getMessage())
                            .setTo(recipients);

                    email.setFrom(mailFrom, mailSender);
                    email.setSubject(notificationRequestDto.getTitle());
                    email.setHostName(mailHostName);
                    email.setSSL(true);
                    email.setSSLOnConnect(true);
                    //email.setSmtpPort(config.getInt("mail.smtpPort"));
                    email.setStartTLSEnabled(false);
                    email.setStartTLSRequired(false);
                    email.setDebug(true);
                    email.setSSLCheckServerIdentity(false);
                    email.setSslSmtpPort(mailSmtpPort);
                    email.setAuthentication(mailUsername, mailPassword);

                    email.send();
                } catch (EmailException e) {
                    logger.error("Error sending login credentials via email.", e);
                }
            } catch (AddressException e) {
                logger.error("Error", e);
            }
        });

    }

    public NotificationResponseDto emailNotificationRequestOption3 (NotificationRequestDto notificationRequestDto){
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
