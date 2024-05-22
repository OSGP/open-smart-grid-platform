// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

class IdentificationNumberTest {

  private final String IDENTIFICATION_NUMBER_AS_STRING = "12049260";
  private final Long IDENTIFICATION_NUMBER_AS_NUMBER = 12049260L;
  private final Long IDENTIFICATION_NUMBER_IN_BCD_AS_LONG = 302289504L;

  private final String IDENTIFICATION_NUMBER_AS_STRING_LARGE = "90000023";
  private final int IDENTIFICATION_NUMBER_AS_INT = -1879048157;

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
  void testIntRepresentation() {

    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromTextualRepresentation(this.IDENTIFICATION_NUMBER_AS_STRING);

    assertThat(identificationNumber.getIntRepresentation())
        .isEqualTo(Integer.parseInt(identificationNumber.getTextualRepresentation(), 16));
  }

  @Test
  void testFromTextualRepresentationIntOverflow() {

    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromTextualRepresentation(this.IDENTIFICATION_NUMBER_AS_STRING_LARGE);

    assertThat(identificationNumber.getIntRepresentation())
        .isEqualTo(this.IDENTIFICATION_NUMBER_AS_INT);
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
