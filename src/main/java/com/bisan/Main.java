package com.bisan;

import com.google.i18n.phonenumbers.NumberParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bisan.Statics.FIJI_NEWZELAND_VENDORS;

public class Main {

  public static void main(String[] args) {
    for (Map.Entry<String, List<String>> entry : Map.of("PS", FIJI_NEWZELAND_VENDORS).entrySet()) {
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

  public static boolean parse(String[] args, int index) throws NumberParseException {
    String number = args[0];

    System.out.println("[" + index + "] NUMBER --- " + number);

    boolean isValid;
    try {
      BPhone bPhone = new BPhone(number);
      isValid = bPhone.isValid();

      System.out.println("bPhone.getRawNumberString()     = " + bPhone.getRawNumberString());
      System.out.println("bPhone.getAreaCode()            = " + bPhone.getAreaCode());
      System.out.println("bPhone.getRegionCode()          = " + BPhone.PHONE_NUMBER_UTIL.getRegionCodeForNumber(bPhone.getPhoneNumber()));
      System.out.println("bPhone.getCountryCode()         = " + bPhone.getCountryCode());
      System.out.println("bPhone.getExtension()           = " + bPhone.getExtension());
      System.out.println("bPhone.getType()                = " + bPhone.getType());
      System.out.println("bPhone.getNationalString()      = " + bPhone.getNationalString());
      System.out.println("bPhone.getInternationalString() = " + bPhone.getInternationalString());
      System.out.println("bPhone.isValid()                = " + bPhone.isValid());
      System.out.println("bPhone.getGeocodedRegionCode()  = " + bPhone.getGeocodedRegionCode());


    } catch (NumberParseException e) {
      System.out.println("error parsing number: " + number);
      throw e;
    }
    return isValid;
  }
}