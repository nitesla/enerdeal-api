package com.enerdeal.helper;

import com.enerdeal.exceptions.ProcessingException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryptions {

    private static final Logger LOG = LoggerFactory.getLogger(Encryptions.class);

    // hashing with HMAC SHA 256
    public static String hashWithHmacSha256(String key, String data) throws ProcessingException {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            byte[] decodedPrivateKey = Hex.decodeHex(key.toCharArray());
            SecretKeySpec secret_key = new SecretKeySpec(decodedPrivateKey, "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
        } catch (Exception ex) {
            String errorMessage = "Unable to hash this string";
            throw new ProcessingException(errorMessage);
        }
    }

    // hashing with SHA 512
    public static String sha512hash(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(data.getBytes());
        byte byteData[] = md.digest();
        StringBuffer hashCodeBuffer = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return hashCodeBuffer.toString();
    }

    // hashing with MD5
    public static String getMd5(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // hashing with SHA 256 to hex
    public static String generateSha256(String originalString){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
            return sha256BytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Unable to generateSha256 of {} ", originalString, e);
        }
        return "";
    }

    private static String sha256BytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
