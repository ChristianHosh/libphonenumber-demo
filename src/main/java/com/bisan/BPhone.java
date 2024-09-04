package com.bisan;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import java.util.Locale;

public class BPhone {

  public static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();
  public static final PhoneNumberOfflineGeocoder OFFLINE_GEOCODER = PhoneNumberOfflineGeocoder.getInstance();

  private final Phonenumber.PhoneNumber phoneNumber;
  private final String areaCode;
  private final String extension;
  private final String rawNumberString;


  public BPhone(String numberString) throws NumberParseException {
    Phonenumber.PhoneNumber tempPhoneNumber;
    
    String[] numberExtension = parseExtension(numberString);
    numberString = numberExtension[0];
    this.extension = numberExtension[1];

    this.rawNumberString = numberString
        .replaceAll("\\D", "")  // clean symbol
        .replaceAll("^0+", ""); // strip leading zeros

    String countryCode = parseCountryCode(numberString);
    String defaultRegion = Locale.getDefault().getCountry();
    
    String regionCode = countryCode.equals(numberString) 
        ? defaultRegion    // if no country code between parentheses then use default country
        : parseRegionCode(countryCode);  // use provided country code
    
    try {
      // if it fails parsing fallback to parsing with a plus sign (for international numbers)
      tempPhoneNumber = PHONE_NUMBER_UTIL.parse(this.rawNumberString, regionCode);
      System.out.println("falling back 1");
    } catch (NumberParseException e) {
      try {
        tempPhoneNumber = PHONE_NUMBER_UTIL.parse("+" + this.rawNumberString, regionCode);
        System.out.println("falling back 2");
      } catch (NumberParseException e1) {
        tempPhoneNumber = PHONE_NUMBER_UTIL.parse("+" + this.rawNumberString, defaultRegion);
        System.out.println("falling back 3");
      }
    }

    this.phoneNumber = tempPhoneNumber;
    this.areaCode = PhoneNumberUtil.PhoneNumberType.FIXED_LINE.equals(this.getType()) ? parseAreaCode(this.phoneNumber) : "";
  }

  public Phonenumber.PhoneNumber getPhoneNumber() {
    return phoneNumber;
  }

  public String getRawNumberString() {
    return rawNumberString;
  }

  public String getAreaCode() {
    return areaCode;
  }

  public String getExtension() {
    return extension;
  }

  public boolean isValid() {
    return PHONE_NUMBER_UTIL.isValidNumber(phoneNumber);
  }

  public String getInternationalString() {
    return PHONE_NUMBER_UTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
  }

  public String getNationalString() {
    return PHONE_NUMBER_UTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
  }

  public String getRFC3966String() {
    return PHONE_NUMBER_UTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
  }

  public String getE164String() {
    return PHONE_NUMBER_UTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
  }

  public PhoneNumberUtil.PhoneNumberType getType() {
    return PHONE_NUMBER_UTIL.getNumberType(phoneNumber);
  }

  public int getCountryCode() {
    return phoneNumber.getCountryCode();
  }

  public String getGeocodedRegionCode() {
    String regionCode = OFFLINE_GEOCODER.getDescriptionForNumber(phoneNumber, Locale.ENGLISH);
    return regionCode != null ? regionCode : "";
  }


  /**
   * returns the region code for the given country code
   *
   * @param countryCode the country code to get the region code for
   * @return the region code for the given country code or null if the country code is invalid
   */
  private static String parseRegionCode(String countryCode) {
    try {
      int regionCodeInt = Integer.parseInt(countryCode); // for example 972, 970, 1,...
      return PHONE_NUMBER_UTIL.getRegionCodeForCountryCode(regionCodeInt);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * returns the country code in the given raw number string
   *
   * @param number the raw number string to extract the country code from
   * @return the country code in the given raw number string
   */
  private static String parseCountryCode(String number) {
    int firstIndex = number.indexOf("(");
    int lastIndex = number.indexOf(")");
    if (firstIndex == -1 || lastIndex == -1) {
      return number;
    }
    String countryCode = number.substring(firstIndex + 1, lastIndex);
    return countryCode.length() == 3 ? countryCode : number;
  }

  private static String parseAreaCode(Phonenumber.PhoneNumber number) {
    // for example for number 022775447 -> area code = 2

    int areaCodeLength = PHONE_NUMBER_UTIL.getLengthOfNationalDestinationCode(number);
    String nationalSignificantNumber = PHONE_NUMBER_UTIL.getNationalSignificantNumber(number);

    return nationalSignificantNumber.substring(0, areaCodeLength);
  }

  private String[] parseExtension(String numberString) {
    int index = numberString.indexOf(":");
    if (index == -1) {
      return new String[]{numberString, ""};
    }

    return new String[]{numberString.substring(0, index), numberString.substring(index + 1)};
  }
}
