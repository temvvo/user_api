package com.user.api_challenge.general.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

  private static final Logger logger = LoggerFactory.getLogger(Utils.class);

  public static String bytesToHex(byte[] hash) {
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < hash.length; i++) {
      String hex = Integer.toHexString(0xff & hash[i]);
      if (hex.length() == 1) hexString.append('0');
      hexString.append(hex);
    }
    return hexString.toString();
  }

  public static String encodePasswordWithSha256(String rawPassword) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedhash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

      return Utils.bytesToHex(encodedhash);
    } catch (NoSuchAlgorithmException e) {
      logger.error("NoSuchAlgorithmException: " + e.getMessage());
      return null;
    }
  }
}
