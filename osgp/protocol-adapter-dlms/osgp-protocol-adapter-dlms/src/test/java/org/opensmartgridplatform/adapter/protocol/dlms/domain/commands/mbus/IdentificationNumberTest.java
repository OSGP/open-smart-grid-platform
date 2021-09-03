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
  private final Long IDENTIFICATION_NUMBER_AS_NUMBER = 12049260L;
  private final Long IDENTIFICATION_NUMBER_IN_BCD_AS_LONG = 302289504L;

  @Test
  void testFromBcdRepresentation() {

    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromBcdRepresentationAsLong(this.IDENTIFICATION_NUMBER_IN_BCD_AS_LONG);

    assertThat(identificationNumber.getTextualRepresentation())
        .isEqualTo(this.IDENTIFICATION_NUMBER_AS_STRING);
  }

  @Test
  void testFromTextualRepresentation() {

    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromTextualRepresentation(this.IDENTIFICATION_NUMBER_AS_STRING);

    assertThat(identificationNumber.getIdentificationNumberInBcdRepresentationAsLong())
        .isEqualTo(this.IDENTIFICATION_NUMBER_IN_BCD_AS_LONG);
  }

  @Test
  void testFromInvalidIdentificationNumberInTextualRepresentation() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumber.fromTextualRepresentation("123A5678");
            });
  }

  @Test
  void testFromIdentificationNumberInTextualRepresentationTooLong() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumber.fromTextualRepresentation("1234567890");
            });
  }

  @Test
  void testFromIdentificationNumberInTextualRepresentationBlank() {
    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromTextualRepresentation("");

    assertThat(identificationNumber.getTextualRepresentation()).isNull();
  }

  @Test
  void testFromIdentificationNumberInTextualRepresentationNull() {
    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromTextualRepresentation(null);

    assertThat(identificationNumber.getTextualRepresentation()).isNull();
  }

  @Test
  void testFromInvalidIdentificationNumberInBcdAsLong() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumber.fromBcdRepresentationAsLong(99999999L);
            });
  }

  @Test
  void testFromIdentificationNumberInBcdAsLongTooLong() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumber.fromBcdRepresentationAsLong(123456789L);
            });
  }

  @Test
  void testFromIdentificationNumberInBcdAsLongTooLow() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumber.fromBcdRepresentationAsLong(-1L);
            });
  }

  @Test
  void testFromNumericalRepresentation() {

    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromNumericalRepresentation(this.IDENTIFICATION_NUMBER_AS_NUMBER);

    assertThat(identificationNumber.getIdentificationNumberInBcdRepresentationAsLong())
        .isEqualTo(this.IDENTIFICATION_NUMBER_IN_BCD_AS_LONG);
  }
}
