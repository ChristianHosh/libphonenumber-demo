package com.bisan;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import java.util.*;

import static com.bisan.Statics.*;

public class Main {

  public static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();
  public static final PhoneNumberOfflineGeocoder OFFLINE_GEOCODER = PhoneNumberOfflineGeocoder.getInstance();


  public static final String CMD_EXIT = "exit";

  public static void main(String[] args) {
    for (Map.Entry<String, List<String>> entry : Map.of("FIJI", FIJI_NUMBERS).entrySet()) {
      int failed = 0;
      int total = 0;
      int success = 0;
      int valid = 0;
      int invalid = 0;
      List<String> failedNumbers = new ArrayList<>();
      List<String> invalidNumbers = new ArrayList<>();

      List<String> stringList = entry.getValue();
      for (int i = 0; i < stringList.size(); i++) {
        String number = stringList.get(i);
        String[] asArgs = new String[]{number};
        boolean isValid;
        try {
          System.out.println("LIST: " + entry.getKey());
          isValid = parse(asArgs, i);
          success++;
        } catch (Exception e) {
          e.printStackTrace(System.out);
          failedNumbers.add(number);
          failed++;
          isValid = false;
        }
        System.out.println();
        total++;
        if (isValid)
          valid++;
        else {
          invalid++;
          invalidNumbers.add(number);
        }

      }

      System.out.println("total: " + total);
      System.out.println("success: " + success);
      System.out.println("failed: " + failed);
      System.out.println("valid: " + valid);
      System.out.println("invalid: " + invalid);
      System.out.println("failed numbers: " + failedNumbers);
      System.out.println("invalid numbers: " + invalidNumbers);
    }
  }


  private static void work() {
    String input;

    try (Scanner scanner = new Scanner(System.in)) {
      do {
        System.out.print("\n> ");
        input = scanner.nextLine();
        if (CMD_EXIT.equals(input)) {
          break;
        }

        if (input.isBlank()) {
          System.err.println("Invalid arguments: <phone number> -<optional country code>");
          continue;
        }

        String[] args = input.split(" -");
        parse(args, 0);
      } while (true);
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }

  public static boolean parse(String[] args, int index) throws NumberParseException {
    String number = args[0];

    System.out.println("[" + index + "] NUMBER: " + number);
    BPhone phone = new BPhone(number);
    handle(phone);
    
    if (true)
      return phone.isValid();

    String countryCode = getCountryCode(number);
    String regionCode = PHONE_NUMBER_UTIL.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
    System.out.println("REGION: " + regionCode);

    boolean isValid;
    try {
      Phonenumber.PhoneNumber phoneNumber = PHONE_NUMBER_UTIL.parse(number, regionCode);
      isValid = PHONE_NUMBER_UTIL.isValidNumber(phoneNumber);

      System.out.println("country code:\t\t\t\t" + phoneNumber.getCountryCode());
      System.out.println("national number:\t\t\t" + phoneNumber.getNationalNumber());
      System.out.println("extension:\t\t\t\t" + phoneNumber.getExtension());
      System.out.println("region:\t\t\t\t\t\t" + PHONE_NUMBER_UTIL.getRegionCodeForNumber(phoneNumber));
      System.out.println("is possible number:\t\t\t" + PHONE_NUMBER_UTIL.isPossibleNumber(phoneNumber));
      System.out.println("is valid number:\t\t\t" + isValid);
      System.out.println("number type:\t\t\t\t" +     PHONE_NUMBER_UTIL.getNumberType(phoneNumber));
      System.out.println("region:\t\t\t\t\t\t" +      OFFLINE_GEOCODER.getDescriptionForNumber(phoneNumber, Locale.ENGLISH));
      System.out.println("FORMATTING RESULTS");
      System.out.println("E164:\t\t\t\t\t\t" +        PHONE_NUMBER_UTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164));
      System.out.println("INTERNATIONAL:\t\t\t\t" +   PHONE_NUMBER_UTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
      System.out.println("NATIONAL:\t\t\t\t\t" +      PHONE_NUMBER_UTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
      System.out.println("RFC3966:\t\t\t\t\t" +       PHONE_NUMBER_UTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966));

    } catch (NumberParseException e) {
      System.err.println("error parsing number: " + number);
      throw e;
    }
    return isValid;
  }

  private static void handle(BPhone phone) {  
    
  }

  private static String getCountryCode(String number) {
    int firstIndex = number.indexOf("(");
    int lastIndex = number.indexOf(")");
    if (firstIndex == -1 || lastIndex == -1) {
      return number;
    }
    return number.substring(firstIndex + 1, lastIndex);
  }
}