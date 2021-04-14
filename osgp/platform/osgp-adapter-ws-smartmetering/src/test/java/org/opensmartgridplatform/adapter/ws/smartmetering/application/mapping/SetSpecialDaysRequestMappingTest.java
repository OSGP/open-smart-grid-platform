/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SpecialDay;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequest;

public class SetSpecialDaysRequestMappingTest {

  private static final String DEVICE_ID = "nr1";
  private static final int DAY_ID = 1;
  private static final byte[] COSEMDATE_BYTE_ARRAY = {(byte) 0x07, (byte) 0xE0, 4, 6, 4};
  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  /**
   * Tests mapping of a SetSpecialDaysRequest object, when its SpecialDaysRequestData object has a
   * filled List (1 entry).
   */
  @Test
  public void testSetSpecialDaysRequestMappingFilledList() {

    // build test data
    final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestData();
    final SpecialDay specialDay = new SpecialDay();
    specialDay.setDayId(DAY_ID);
    specialDay.setSpecialDayDate(COSEMDATE_BYTE_ARRAY);
    // To add a SpecialDay to the List, you need to use the getter in
    // combination with add()
    specialDaysRequestData.getSpecialDays().add(specialDay);
    final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
    setSpecialDaysRequest.setDeviceIdentification(DEVICE_ID);
    setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);

    // actual mapping
    final SpecialDaysRequest specialDaysRequest =
        this.configurationMapper.map(setSpecialDaysRequest, SpecialDaysRequest.class);

    // check mapping
    assertThat(specialDaysRequest).isNotNull();
    assertThat(specialDaysRequest.getDeviceIdentification()).isNotNull();
    assertThat(specialDaysRequest.getSpecialDaysRequestData()).isNotNull();
    assertThat(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays()).isNotNull();
    assertThat(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().get(0)).isNotNull();
    assertThat(
            specialDaysRequest
                .getSpecialDaysRequestData()
                .getSpecialDays()
                .get(0)
                .getSpecialDayDate())
        .isNotNull();

    assertThat(specialDaysRequest.getDeviceIdentification()).isEqualTo(DEVICE_ID);
    assertThat(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().size())
        .isEqualTo(specialDaysRequestData.getSpecialDays().size());
    assertThat(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().get(0).getDayId())
        .isEqualTo(DAY_ID);

    // For more info on the mapping of byte[] to CosemDate object, see the
    // CosemDateConverterTest.
  }

  /**
   * Tests mapping of a SetSpecialDaysRequest, when its SpecialDaysRequestData object has an empty
   * List.
   */
  @Test
  public void testSpecialDaysRequestMappingEmptyList() {

    // build test data
    // No-arg constructor for SpecialDaysRequestData takes care of creating
    // a empty List.
    final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestData();
    final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
    setSpecialDaysRequest.setDeviceIdentification(DEVICE_ID);
    setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);

    // actual mapping
    final SpecialDaysRequest specialDaysRequest =
        this.configurationMapper.map(setSpecialDaysRequest, SpecialDaysRequest.class);

    // check mapping
    assertThat(specialDaysRequest).isNotNull();
    assertThat(specialDaysRequest.getDeviceIdentification()).isNotNull();
    assertThat(specialDaysRequest.getSpecialDaysRequestData()).isNotNull();
    assertThat(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays()).isNotNull();
    assertThat(specialDaysRequest.getDeviceIdentification()).isEqualTo(DEVICE_ID);
    assertThat(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().isEmpty()).isTrue();
  }

  /**
   * Tests mapping of a SetSpecialDaysRequest object, when its SetSpecialDaysRequest object is null.
   */
  @Test
  public void testSpecialDaysRequestMappingNull() {

    // build test data
    final SpecialDaysRequestData specialDaysRequestData = null;
    final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
    setSpecialDaysRequest.setDeviceIdentification(DEVICE_ID);
    setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);

    // actual mapping
    final SpecialDaysRequest specialDaysRequest =
        this.configurationMapper.map(setSpecialDaysRequest, SpecialDaysRequest.class);

    // check mapping
    assertThat(specialDaysRequest.getDeviceIdentification()).isEqualTo(DEVICE_ID);
    assertThat(specialDaysRequest.getSpecialDaysRequestData()).isNull();
  }

  /**
   * Tests mapping of a SpecialDaysRequesData object. A NullPointerException should be thrown when
   * no byte[] is specified for SpecialDay.
   */
  @Test
  public void testWithoutByteArray() {
    // build test data
    final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestData();
    final SpecialDay specialDay = new SpecialDay();
    specialDay.setDayId(DAY_ID);
    // To add a SpecialDay to the List, you need to use the getter in
    // combination with add()
    specialDaysRequestData.getSpecialDays().add(specialDay);
    final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
    setSpecialDaysRequest.setDeviceIdentification(DEVICE_ID);
    setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              // actual mapping
              this.configurationMapper.map(setSpecialDaysRequest, SpecialDaysRequest.class);
            });
  }
}
