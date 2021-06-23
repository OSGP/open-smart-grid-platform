/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

/**
 * Handles creation and validation of @{@link IdentificationNumber} based on either the String
 * representation or the Number representation.
 */
public class IdentificationNumberFactory {

  private static final int HEX_RADIX = 16;
  private static final String IDENTIFICATION_NUMBER_REGEX_DSMR4 = "\\d{1,8}";
  private static final String IDENTIFICATION_NUMBER_REGEX_SMR5 = "[0-9A-Fa-f]{1,8}";
  private static final long MIN_IDENTIFICATION = 0;
  private static final long MAX_IDENTIFICATION = Long.parseLong("99999999", HEX_RADIX);

  private final Protocol protocol;

  private IdentificationNumberFactory(final Protocol protocol) {
    this.protocol = protocol;
  }

  public static IdentificationNumberFactory create(final Protocol protocol) {
    return new IdentificationNumberFactory(protocol);
  }

  private void validateLast8Digits(final String last8Digits) {
    if (StringUtils.isNotBlank(last8Digits) && !this.matches(last8Digits)) {
      throw new IllegalArgumentException(
          String.format("IdentificationNumber %s did not pass validation.", last8Digits));
    }
  }

  private boolean matches(final String last8Digits) {
    if (this.protocol.isSmr5()) {
      return last8Digits.matches(IDENTIFICATION_NUMBER_REGEX_SMR5);
    }
    return last8Digits.matches(IDENTIFICATION_NUMBER_REGEX_DSMR4);
  }

  IdentificationNumber fromIdentification(final Long identification) {
    final String last8Digits = this.fromLong(identification);
    this.validateLast8Digits(last8Digits);

    return new IdentificationNumber(last8Digits);
  }

  public IdentificationNumber fromLast8Digits(final String digits) {
    final String last8Digits = this.fromString(digits);
    this.validateLast8Digits(last8Digits);

    return new IdentificationNumber(last8Digits);
  }

  private String fromLong(final Long identification) {
    if (identification == null) {
      return null;
    }
    if (identification < MIN_IDENTIFICATION || identification > MAX_IDENTIFICATION) {
      throw new IllegalArgumentException(
          String.format(
              "identification not in [%d..%d]: %d",
              MIN_IDENTIFICATION, MAX_IDENTIFICATION, identification));
    }
    return String.format("%08X", identification);
  }

  private String fromString(final String digits) {
    if (StringUtils.isBlank(digits)) {
      return null;
    } else {
      /*
       * If a String of less than 8 digits is given, make sure it is
       * prefixed with zero digits up to a length of 8.
       */
      return StringUtils.leftPad(digits, 8);
    }
  }
}
