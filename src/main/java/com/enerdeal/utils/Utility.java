package com.enerdeal.utils;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("ALL")
@Slf4j
public class Utility {




    public static final String APP_NAME = "ENERDEAL";




    public static boolean containSpecialCharacter(String unsafeInput) {

        boolean specialCharacterFound = false;
        Pattern p = Pattern.compile("[^A-Za-z0-9-']");
        Matcher m = p.matcher(unsafeInput);
        if (m.find()) {
            specialCharacterFound = true;
        }
        return specialCharacterFound;
    }


    public static Date convertDate(String dateString) throws ParseException {

        SimpleDateFormat format1 = new SimpleDateFormat("ddMMyyyy");
        Date date = format1.parse(dateString);
        return date;
    }




    public static boolean validEmail(String email) {
        String emailRegex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if (email.matches(emailRegex))
            return true;
        else
            return false;
    }


    public static boolean isNumeric(String number) {
        boolean isValid = false;

        String expression = "^[-+]?[0-9]*\\.?[0-9]+$";
        CharSequence inputStr = number;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    public static String getSaltString() {
        return UUID.randomUUID().toString().substring(0, 5) + UUID.randomUUID().toString().substring(0, 3);
    }


    public static String guidID() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }



    public static String getIpAddress(HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        ipAddress = ipAddress == null ? httpRequest.getRemoteAddr() : ipAddress;
        return ipAddress;
    }

    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }





    public static boolean validateName(String name) {
        String pattern = "^[a-zA-Z-']*$";
        return name.matches(pattern);
    }

    public static boolean validateEnableDisable(Boolean status) {
        String pattern = "^true$|^false$";
//        return status.matches(pattern);
        return status.toString(status).matches(pattern);
    }







    public static int computeAge(String dateOfBirth) throws Exception {

        final Long MILLI_SECONDS_YEAR = 31558464000L;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");

        Date dbDate = dateFormat.parse(dateOfBirth);

        // Compute age.
        long timeDiff = System.currentTimeMillis() - dbDate.getTime();
        int age = (int) (timeDiff / MILLI_SECONDS_YEAR);  //
        return age;
    }

    public static boolean isAlphaNumeric(String s) {
        String pattern = "^[a-zA-Z0-9]*$";
        if (s.matches(pattern)) {
            return true;
        }
        return false;
    }






    public static boolean validatePhoneNumber(String unsafePhone) {

        Pattern pattern = Pattern.compile("\\d+"); //accepts only digits
        Matcher matcher = pattern.matcher(unsafePhone);

        boolean valid = false;

        if (matcher.matches()) {
            valid = true;
        }
        return valid;
    }

    public static boolean containsAlphabet(String unsafeInput) {
        Pattern pattern = Pattern.compile(".*[a-zA-Z].*");
        Matcher matcher = pattern.matcher(unsafeInput);
        boolean valid = false;
        if (matcher.matches()) {
            valid = true;
        }
        return valid;
    }







    public static String registrationCode(String dateFormat) {
        String[] strNow = new SimpleDateFormat(dateFormat).format(new Date()).split("-");
        String str="";
        for (String string : strNow) {
            str=str+string;
        }
        return str;
    }


    public static String expiredTime() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.MINUTE, 30);
        return String.valueOf(new Timestamp(cal.getTime().getTime()));
    }


    public static String tokenExpiration() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.MINUTE, 30);
        return String.valueOf(new Timestamp(cal.getTime().getTime()));
    }



    public static String passwordExpiration() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.MINUTE, 10);
        return String.valueOf(new Timestamp(cal.getTime().getTime()));
    }


    public static String passwordGeneration() {
        String SALTCHARS = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();

        return saltStr;
    }
}
