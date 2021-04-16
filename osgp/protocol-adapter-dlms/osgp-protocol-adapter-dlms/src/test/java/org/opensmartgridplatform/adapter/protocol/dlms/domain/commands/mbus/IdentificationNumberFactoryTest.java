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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class IdentificationNumberFactoryTest {

  @Test
  public void testFromIdentificationDsmr4() {

    final String last8Digits = "12049260";
    final Long identification = 302289504L;

    final IdentificationNumber identificationNumber =
        IdentificationNumberFactory.create(Protocol.DSMR_4_2_2).fromIdentification(identification);

    assertThat(identificationNumber.getLast8Digits()).isEqualTo(last8Digits);
  }

  @Test
  public void testFromLast8DigitsDsmr4() {

    final String last8Digits = "12049260";
    final Long identification = 302289504L;

    final IdentificationNumber identificationNumber =
        IdentificationNumberFactory.create(Protocol.DSMR_4_2_2).fromLast8Digits(last8Digits);

    assertThat(identificationNumber.getIdentificationNumber()).isEqualTo(identification);
  }

  @Test
  public void testFromInvalidLast8DigitsDsmr4() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumberFactory.create(Protocol.DSMR_4_2_2).fromLast8Digits("123A5678");
            });
  }

  @Test
  public void testFromInvalidIdentificationDsmr4() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumberFactory.create(Protocol.DSMR_4_2_2)
                  .fromIdentification(123456789L);
            });
  }

  @Test
  public void testFromIdentificationSmr5() {

    final String last8Digits = "12345BEF";
    final Long identification = 305421295L;

    final IdentificationNumber identificationNumber =
        IdentificationNumberFactory.create(Protocol.SMR_5_0_0).fromIdentification(identification);

    assertThat(identificationNumber.getLast8Digits()).isEqualTo(last8Digits);
  }

  @Test
  public void testFromLast8DigitsSmr5() {

    final String last8Digits = "12345BEF";
    final Long identification = 305421295L;

    final IdentificationNumber identificationNumber =
        IdentificationNumberFactory.create(Protocol.SMR_5_0_0).fromLast8Digits(last8Digits);

    assertThat(identificationNumber.getIdentificationNumber()).isEqualTo(identification);
  }

  @Test
  public void testFromInvalidLast8DigitsSmr5() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumberFactory.create(Protocol.SMR_5_0_0).fromLast8Digits("1234S678");
            });
  }

  @Test
  public void testFromInvalidIdentificationSmr5() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              IdentificationNumberFactory.create(Protocol.SMR_5_0_0)
                  .fromIdentification(123456789100L);
            });
  }

  @Test
  public void testFromNullIdentificationSmr51() {

    final IdentificationNumber identificationNumber =
        IdentificationNumberFactory.create(Protocol.SMR_5_1).fromIdentification(null);

    assertThat(identificationNumber.getLast8Digits()).isNull();
  }

  @Test
  public void testFromNullLast8DigitsSmr51() {

    final IdentificationNumber identificationNumber =
        IdentificationNumberFactory.create(Protocol.SMR_5_1).fromLast8Digits(null);

    assertThat(identificationNumber.getIdentificationNumber()).isNull();
  }
}
