/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

class IdentificationNumberTest {

  private final String IDENTIFICATION_NUMBER_AS_STRING = "12049260";
  private final Long IDENTIFICATION_NUMBER_IN_BCD_AS_LONG = 302289504L;

  @Test
  void testFromBcdFormat() {

    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromBcdFormatAsLong(this.IDENTIFICATION_NUMBER_IN_BCD_AS_LONG);

    assertThat(identificationNumber.getStringRepresentation())
        .isEqualTo(this.IDENTIFICATION_NUMBER_AS_STRING);
  }

  @Test
  void testFromString() {

    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromStringRepresentation(this.IDENTIFICATION_NUMBER_AS_STRING);

    assertThat(identificationNumber.getIdentificationNumberInBcdAsLong())
        .isEqualTo(this.IDENTIFICATION_NUMBER_IN_BCD_AS_LONG);
  }

  @Test
  void testFromInvalidIdentificationNumberAsString() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumber.fromStringRepresentation("123A5678");
            });
  }

  @Test
  void testFromIdentificationNumberAsStringTooLong() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumber.fromStringRepresentation("1234567890");
            });
  }

  @Test
  void testFromIdentificationNumberAsStringBlank() {
    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromStringRepresentation("");

    assertThat(identificationNumber.getStringRepresentation()).isNull();
  }

  @Test
  void testFromIdentificationNumberAsStringNull() {
    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromStringRepresentation(null);

    assertThat(identificationNumber.getStringRepresentation()).isNull();
  }

  @Test
  void testFromInvalidIdentificationNumberAsLong() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumber.fromBcdFormatAsLong(99999999L);
            });
  }

  @Test
  void testFromIdentificationNumberAsLongTooLong() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumber.fromBcdFormatAsLong(123456789L);
            });
  }

  @Test
  void testFromIdentificationNumberAsLongTooLow() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumber.fromBcdFormatAsLong(-1L);
            });
  }
}
