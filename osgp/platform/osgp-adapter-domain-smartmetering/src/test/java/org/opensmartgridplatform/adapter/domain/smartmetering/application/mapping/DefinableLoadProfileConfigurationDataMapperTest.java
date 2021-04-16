/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObjectDefinition;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DefinableLoadProfileConfigurationData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DefinableLoadProfileConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;

public class DefinableLoadProfileConfigurationDataMapperTest {

  private static final Long CAPTURE_PERIOD = 86400L;

  private static final int NUMBER_OF_CAPTURE_OBJECTS = 2;

  private static final int CAPTURE_OBJECT_CLASS_ID_1 = 1;
  private static final byte CAPTURE_OBJECT_OBIS_A_1 = 1;
  private static final byte CAPTURE_OBJECT_OBIS_B_1 = 0;
  private static final byte CAPTURE_OBJECT_OBIS_C_1 = 52;
  private static final byte CAPTURE_OBJECT_OBIS_D_1 = 32;
  private static final byte CAPTURE_OBJECT_OBIS_E_1 = 0;
  private static final byte CAPTURE_OBJECT_OBIS_F_1 = (byte) 255;
  private static final byte CAPTURE_OBJECT_ATTRIBUTE_INDEX_1 = 2;
  private static final Integer CAPTURE_OBJECT_DATA_INDEX_1 = 0;

  private static final int CAPTURE_OBJECT_CLASS_ID_2 = 3;
  private static final byte CAPTURE_OBJECT_OBIS_A_2 = 1;
  private static final byte CAPTURE_OBJECT_OBIS_B_2 = 0;
  private static final byte CAPTURE_OBJECT_OBIS_C_2 = 32;
  private static final byte CAPTURE_OBJECT_OBIS_D_2 = 7;
  private static final byte CAPTURE_OBJECT_OBIS_E_2 = 0;
  private static final byte CAPTURE_OBJECT_OBIS_F_2 = (byte) 255;
  private static final byte CAPTURE_OBJECT_ATTRIBUTE_INDEX_2 = 2;
  private static final Integer CAPTURE_OBJECT_DATA_INDEX_2 = 0;

  private final ConfigurationMapper mapper = new ConfigurationMapper();

  @Test
  public void testDefinableLoadProfileConfigurationMapping() {
    final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData =
        this.newDefinableLoadProfileConfigurationData();

    final DefinableLoadProfileConfigurationDto definableLoadProfileConfigurationDto =
        this.mapper.map(
            definableLoadProfileConfigurationData, DefinableLoadProfileConfigurationDto.class);

    assertThat(definableLoadProfileConfigurationDto)
        .withFailMessage("Result of mapping DefinableLoadProfileConfigurationData must not be null")
        .isNotNull();

    assertThat(definableLoadProfileConfigurationDto.hasCaptureObjects())
        .withFailMessage(
            "DefinableLoadProfileConfigurationDto should have capture objects: "
                + definableLoadProfileConfigurationDto)
        .isTrue();

    this.assertCaptureObjects(definableLoadProfileConfigurationDto.getCaptureObjects());
    assertThat(definableLoadProfileConfigurationDto.hasCapturePeriod())
        .withFailMessage(
            "DefinableLoadProfileConfigurationDto should have a capture period: "
                + definableLoadProfileConfigurationDto)
        .isTrue();

    assertThat(definableLoadProfileConfigurationDto.getCapturePeriod())
        .withFailMessage("DefinableLoadProfileConfigurationDto capture period")
        .isEqualTo(CAPTURE_PERIOD);
  }

  private DefinableLoadProfileConfigurationData newDefinableLoadProfileConfigurationData() {
    final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData =
        new DefinableLoadProfileConfigurationData(this.newCaptureObjects(), CAPTURE_PERIOD);
    return definableLoadProfileConfigurationData;
  }

  private List<CaptureObjectDefinition> newCaptureObjects() {
    final List<CaptureObjectDefinition> captureObjects = new ArrayList<>();
    captureObjects.add(
        this.newCaptureObjectDefinition(
            CAPTURE_OBJECT_CLASS_ID_1,
            this.newLogicalName(
                CAPTURE_OBJECT_OBIS_A_1,
                CAPTURE_OBJECT_OBIS_B_1,
                CAPTURE_OBJECT_OBIS_C_1,
                CAPTURE_OBJECT_OBIS_D_1,
                CAPTURE_OBJECT_OBIS_E_1,
                CAPTURE_OBJECT_OBIS_F_1),
            CAPTURE_OBJECT_ATTRIBUTE_INDEX_1,
            CAPTURE_OBJECT_DATA_INDEX_1));
    captureObjects.add(
        this.newCaptureObjectDefinition(
            CAPTURE_OBJECT_CLASS_ID_2,
            this.newLogicalName(
                CAPTURE_OBJECT_OBIS_A_2,
                CAPTURE_OBJECT_OBIS_B_2,
                CAPTURE_OBJECT_OBIS_C_2,
                CAPTURE_OBJECT_OBIS_D_2,
                CAPTURE_OBJECT_OBIS_E_2,
                CAPTURE_OBJECT_OBIS_F_2),
            CAPTURE_OBJECT_ATTRIBUTE_INDEX_2,
            CAPTURE_OBJECT_DATA_INDEX_2));
    return captureObjects;
  }

  private void assertCaptureObjects(final List<CaptureObjectDefinitionDto> captureObjects) {
    assertThat(captureObjects).withFailMessage("Capture objects").isNotNull();
    assertThat(captureObjects.size())
        .withFailMessage("Number of capture objects")
        .isEqualTo(NUMBER_OF_CAPTURE_OBJECTS);
    this.assertCaptureObjectDefinition(
        1,
        captureObjects.get(0),
        CAPTURE_OBJECT_CLASS_ID_1,
        CAPTURE_OBJECT_OBIS_A_1,
        CAPTURE_OBJECT_OBIS_B_1,
        CAPTURE_OBJECT_OBIS_C_1,
        CAPTURE_OBJECT_OBIS_D_1,
        CAPTURE_OBJECT_OBIS_E_1,
        CAPTURE_OBJECT_OBIS_F_1,
        CAPTURE_OBJECT_ATTRIBUTE_INDEX_1,
        CAPTURE_OBJECT_DATA_INDEX_1);
    this.assertCaptureObjectDefinition(
        2,
        captureObjects.get(1),
        CAPTURE_OBJECT_CLASS_ID_2,
        CAPTURE_OBJECT_OBIS_A_2,
        CAPTURE_OBJECT_OBIS_B_2,
        CAPTURE_OBJECT_OBIS_C_2,
        CAPTURE_OBJECT_OBIS_D_2,
        CAPTURE_OBJECT_OBIS_E_2,
        CAPTURE_OBJECT_OBIS_F_2,
        CAPTURE_OBJECT_ATTRIBUTE_INDEX_2,
        CAPTURE_OBJECT_DATA_INDEX_2);
  }

  private CaptureObjectDefinition newCaptureObjectDefinition(
      final int classId,
      final ObisCodeValues logicalName,
      final byte attributeIndex,
      final Integer dataIndex) {
    return new CaptureObjectDefinition(classId, logicalName, attributeIndex, dataIndex);
  }

  private void assertCaptureObjectDefinition(
      final int captureObjectNumber,
      final CaptureObjectDefinitionDto captureObject,
      final int classId,
      final byte a,
      final byte b,
      final byte c,
      final byte d,
      final byte e,
      final byte f,
      final byte attributeIndex,
      final Integer dataIndex) {
    final String captureObjectDescription = "capture object " + captureObjectNumber + " - ";
    assertThat(captureObject.getClassId())
        .withFailMessage(captureObjectDescription + "class id")
        .isEqualTo(classId);
    this.assertLogicalName(
        captureObjectDescription + "OBIS code value ",
        captureObject.getLogicalName(),
        a,
        b,
        c,
        d,
        e,
        f);
    assertThat(captureObject.getAttributeIndex())
        .withFailMessage(captureObjectDescription + "attribute index")
        .isEqualTo(attributeIndex);
    assertThat(captureObject.getDataIndex())
        .withFailMessage(captureObjectDescription + "data index")
        .isEqualTo(dataIndex);
  }

  private ObisCodeValues newLogicalName(
      final byte a, final byte b, final byte c, final byte d, final byte e, final byte f) {
    return new ObisCodeValues(a, b, c, d, e, f);
  }

  private void assertLogicalName(
      final String obisCodeDescription,
      final ObisCodeValuesDto logicalName,
      final byte a,
      final byte b,
      final byte c,
      final byte d,
      final byte e,
      final byte f) {
    assertThat(logicalName.getA()).withFailMessage(obisCodeDescription + "a").isEqualTo(a);
    assertThat(logicalName.getB()).withFailMessage(obisCodeDescription + "b").isEqualTo(b);
    assertThat(logicalName.getC()).withFailMessage(obisCodeDescription + "c").isEqualTo(c);
    assertThat(logicalName.getD()).withFailMessage(obisCodeDescription + "d").isEqualTo(d);
    assertThat(logicalName.getE()).withFailMessage(obisCodeDescription + "e").isEqualTo(e);
    assertThat(logicalName.getF()).withFailMessage(obisCodeDescription + "f").isEqualTo(f);
  }
}
