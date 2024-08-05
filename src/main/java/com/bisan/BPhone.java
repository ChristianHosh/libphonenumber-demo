package com.bisan;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import java.util.Locale;
import java.util.Objects;

public class BPhone {

  public static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();
  public static final PhoneNumberOfflineGeocoder OFFLINE_GEOCODER = PhoneNumberOfflineGeocoder.getInstance();

  private final Phonenumber.PhoneNumber phoneNumber;
  private final String numberString;
  private final String countryCode;
  private final String regionCode;
  private boolean valid;

  public BPhone(String phoneNumber) {
    Phonenumber.PhoneNumber tempPhoneNumber;

    this.numberString = phoneNumber;
    this.countryCode = parseCountryCode();
    this.regionCode = PHONE_NUMBER_UTIL.getRegionCodeForCountryCode(Integer.parseInt(this.countryCode));

    try {
      tempPhoneNumber = parsePhoneNumber();
      valid = true;
    } catch (NumberParseException e) {
      tempPhoneNumber = null;
      valid = false;
    }

    this.phoneNumber = tempPhoneNumber;
  }

  private Phonenumber.PhoneNumber parsePhoneNumber() throws NumberParseException {
    return PHONE_NUMBER_UTIL.parse(this.numberString, this.regionCode);
  }

  private String parseCountryCode() {
    int firstIndex = this.numberString.indexOf("(");
    int lastIndex = this.numberString.indexOf(")");
    if (firstIndex == -1 || lastIndex == -1) {
      throw new IllegalArgumentException("country code must be between parentheses");
    }

    return this.numberString.substring(firstIndex + 1, lastIndex);
  }

  public boolean isValid() {
    return valid && PHONE_NUMBER_UTIL.isValidNumber(this.phoneNumber);
  }

  public boolean isPossibleNumber() {
    return valid && PHONE_NUMBER_UTIL.isPossibleNumber(this.phoneNumber);
  }

  public PhoneNumberUtil.PhoneNumberType getType() {
    return this.phoneNumber == null ? null : PHONE_NUMBER_UTIL.getNumberType(this.phoneNumber);
  }

  public String getCountryName() {
    return getCountryName(Locale.getDefault());
  }

  public String getCountryName(Locale locale) {
    return this.phoneNumber == null ? null : OFFLINE_GEOCODER.getDescriptionForNumber(this.phoneNumber, locale);
  }

  public Phonenumber.PhoneNumber getPhoneNumber() {
    return this.phoneNumber;
  }

  public String getNumberString() {
    return this.numberString;
  }

  public String getCountryCode() {
    return this.countryCode;
  }

  public String getRegionCode() {
    return this.regionCode;
  }

  public String getInternationalString() {
    return this.phoneNumber == null ? null : PHONE_NUMBER_UTIL.format(this.phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
  }

  public String getNationalString() {
    return this.phoneNumber == null ? null : PHONE_NUMBER_UTIL.format(this.phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
  }

  public String getRFC3966String() {
    return this.phoneNumber == null ? null : PHONE_NUMBER_UTIL.format(this.phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
  }

  public String getE164String() {
    return this.phoneNumber == null ? null : PHONE_NUMBER_UTIL.format(this.phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
  }

  @Override
  public String toString() {
    return this.getInternationalString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof BPhone)) return false;
    return Objects.equals(this.phoneNumber, ((BPhone) obj).phoneNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.phoneNumber);
  }
}
