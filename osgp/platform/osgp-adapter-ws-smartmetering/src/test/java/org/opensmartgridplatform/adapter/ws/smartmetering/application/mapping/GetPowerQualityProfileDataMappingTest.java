/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObjectDefinition;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DefinableLoadProfileConfigurationData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;

public class GetPowerQualityProfileDataMappingTest {

  private static final Long CAPTURE_PERIOD = 86400L;

  private static final int NUMBER_OF_CAPTURE_OBJECTS = 2;

  private static final int CAPTURE_OBJECT_CLASS_ID_1 = 1;
  private static final short CAPTURE_OBJECT_OBIS_A_1 = 1;
  private static final short CAPTURE_OBJECT_OBIS_B_1 = 0;
  private static final short CAPTURE_OBJECT_OBIS_C_1 = 52;
  private static final short CAPTURE_OBJECT_OBIS_D_1 = 32;
  private static final short CAPTURE_OBJECT_OBIS_E_1 = 0;
  private static final short CAPTURE_OBJECT_OBIS_F_1 = 255;
  private static final byte CAPTURE_OBJECT_ATTRIBUTE_INDEX_1 = 2;
  private static final Integer CAPTURE_OBJECT_DATA_INDEX_1 = 0;

  private static final int CAPTURE_OBJECT_CLASS_ID_2 = 3;
  private static final short CAPTURE_OBJECT_OBIS_A_2 = 1;
  private static final short CAPTURE_OBJECT_OBIS_B_2 = 0;
  private static final short CAPTURE_OBJECT_OBIS_C_2 = 32;
  private static final short CAPTURE_OBJECT_OBIS_D_2 = 7;
  private static final short CAPTURE_OBJECT_OBIS_E_2 = 0;
  private static final short CAPTURE_OBJECT_OBIS_F_2 = 255;
  private static final byte CAPTURE_OBJECT_ATTRIBUTE_INDEX_2 = 2;
  private static final Integer CAPTURE_OBJECT_DATA_INDEX_2 = 0;

  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  private void assertCaptureObjectDefinition(
      final int captureObjectNumber,
      final CaptureObjectDefinition captureObject,
      final int classId,
      final short a,
      final short b,
      final short c,
      final short d,
      final short e,
      final short f,
      final byte attributeIndex,
      final Integer dataIndex) {
    final String captureObjectDescription = "capture object " + captureObjectNumber + " - ";
    assertThat(captureObject.getClassId())
        .as(captureObjectDescription + "class id")
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
        .as(captureObjectDescription + "attribute index")
        .isEqualTo(attributeIndex);
    assertThat(captureObject.getDataIndex())
        .as(captureObjectDescription + "data index")
        .isEqualTo(dataIndex);
  }

  private void assertCaptureObjects(final List<CaptureObjectDefinition> captureObjects) {
    assertThat(captureObjects).as("Capture objects").isNotNull();

    assertThat(captureObjects.size())
        .as("Number of capture objects")
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

  private void assertLogicalName(
      final String obisCodeDescription,
      final ObisCodeValues logicalName,
      final short a,
      final short b,
      final short c,
      final short d,
      final short e,
      final short f) {
    assertThat(logicalName.getA()).as(obisCodeDescription + "a").isEqualTo((byte) a);
    assertThat(logicalName.getB()).as(obisCodeDescription + "b").isEqualTo((byte) b);
    assertThat(logicalName.getC()).as(obisCodeDescription + "c").isEqualTo((byte) c);
    assertThat(logicalName.getD()).as(obisCodeDescription + "d").isEqualTo((byte) d);
    assertThat(logicalName.getE()).as(obisCodeDescription + "e").isEqualTo((byte) e);
    assertThat(logicalName.getF()).as(obisCodeDescription + "f").isEqualTo((byte) f);
  }

  private org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinition
      newCaptureObjectDefinition(
          final int classId,
          final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues
              obisCodeValues,
          final byte attributeIndex,
          final Integer dataIndex) {
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinition
        captureObjectDefinition =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.common
                .CaptureObjectDefinition();
    captureObjectDefinition.setClassId(classId);
    captureObjectDefinition.setLogicalName(obisCodeValues);
    captureObjectDefinition.setAttributeIndex(attributeIndex);
    captureObjectDefinition.setDataIndex(dataIndex);
    return captureObjectDefinition;
  }

  private org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions
      newCaptureObjects() {
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions
        captureObjectDefinitions =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.common
                .CaptureObjectDefinitions();
    final List<
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.common
                .CaptureObjectDefinition>
        captureObjects = captureObjectDefinitions.getCaptureObject();
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
    return captureObjectDefinitions;
  }

  private org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
          .DefinableLoadProfileConfigurationData
      newDefinableLoadProfileConfigurationData() {
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .DefinableLoadProfileConfigurationData
        definableLoadProfileConfigurationData =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .DefinableLoadProfileConfigurationData();
    definableLoadProfileConfigurationData.setCaptureObjects(this.newCaptureObjects());
    definableLoadProfileConfigurationData.setCapturePeriod(CAPTURE_PERIOD);
    return definableLoadProfileConfigurationData;
  }

  private org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
          .DefinableLoadProfileConfigurationData
      newDefinableLoadProfileConfigurationDataCaptureObjectsOnly() {
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .DefinableLoadProfileConfigurationData
        definableLoadProfileConfigurationData =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .DefinableLoadProfileConfigurationData();
    definableLoadProfileConfigurationData.setCaptureObjects(this.newCaptureObjects());
    definableLoadProfileConfigurationData.setCapturePeriod(null);
    return definableLoadProfileConfigurationData;
  }

  private org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
          .DefinableLoadProfileConfigurationData
      newDefinableLoadProfileConfigurationDataCapturePeriodOnly() {
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .DefinableLoadProfileConfigurationData
        definableLoadProfileConfigurationData =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .DefinableLoadProfileConfigurationData();
    definableLoadProfileConfigurationData.setCaptureObjects(null);
    definableLoadProfileConfigurationData.setCapturePeriod(CAPTURE_PERIOD);
    return definableLoadProfileConfigurationData;
  }

  private org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues
      newLogicalName(
          final short a,
          final short b,
          final short c,
          final short d,
          final short e,
          final short f) {
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues
        obisCodeValues =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues();
    obisCodeValues.setA(a);
    obisCodeValues.setB(b);
    obisCodeValues.setC(c);
    obisCodeValues.setD(d);
    obisCodeValues.setE(e);
    obisCodeValues.setF(f);
    return obisCodeValues;
  }

  @Test
  public void testDefinableLoadProfileConfiguration() {

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .DefinableLoadProfileConfigurationData
        wsDefinableLoadProfileConfigurationData = this.newDefinableLoadProfileConfigurationData();

    final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData =
        this.configurationMapper.map(
            wsDefinableLoadProfileConfigurationData, DefinableLoadProfileConfigurationData.class);

    assertThat(definableLoadProfileConfigurationData)
        .as("Result of mapping DefinableLoadProfileConfigurationData must not be null")
        .isNotNull();
    assertThat(definableLoadProfileConfigurationData.hasCaptureObjects())
        .as(
            "DefinableLoadProfileConfigurationData should have capture objects: "
                + definableLoadProfileConfigurationData)
        .isTrue();

    this.assertCaptureObjects(definableLoadProfileConfigurationData.getCaptureObjects());
    assertThat(definableLoadProfileConfigurationData.hasCapturePeriod())
        .as(
            "DefinableLoadProfileConfigurationData should have a capture period: "
                + definableLoadProfileConfigurationData)
        .isTrue();
    assertThat(definableLoadProfileConfigurationData.getCapturePeriod())
        .as("DefinableLoadProfileConfigurationData capture period")
        .isEqualTo(CAPTURE_PERIOD);
  }

  @Test
  public void testDefinableLoadProfileConfigurationCaptureObjectsOnly() {

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .DefinableLoadProfileConfigurationData
        wsDefinableLoadProfileConfigurationData =
            this.newDefinableLoadProfileConfigurationDataCaptureObjectsOnly();

    final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData =
        this.configurationMapper.map(
            wsDefinableLoadProfileConfigurationData, DefinableLoadProfileConfigurationData.class);

    assertThat(definableLoadProfileConfigurationData)
        .as("Result of mapping DefinableLoadProfileConfigurationData must not be null")
        .isNotNull();
    assertThat(definableLoadProfileConfigurationData.hasCaptureObjects())
        .as(
            "DefinableLoadProfileConfigurationData with capture objects only must contain capture objects: "
                + definableLoadProfileConfigurationData)
        .isTrue();

    this.assertCaptureObjects(definableLoadProfileConfigurationData.getCaptureObjects());
    assertThat(definableLoadProfileConfigurationData.hasCapturePeriod())
        .as(
            "DefinableLoadProfileConfigurationData with capture objects only must not contain capture period: "
                + definableLoadProfileConfigurationData)
        .isFalse();
  }

  @Test
  public void testDefinableLoadProfileConfigurationCapturePeriodOnly() {

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .DefinableLoadProfileConfigurationData
        wsDefinableLoadProfileConfigurationData =
            this.newDefinableLoadProfileConfigurationDataCapturePeriodOnly();

    final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData =
        this.configurationMapper.map(
            wsDefinableLoadProfileConfigurationData, DefinableLoadProfileConfigurationData.class);

    assertThat(definableLoadProfileConfigurationData)
        .as("Result of mapping DefinableLoadProfileConfigurationData must not be null")
        .isNotNull();
    assertThat(definableLoadProfileConfigurationData.hasCaptureObjects())
        .as(
            "DefinableLoadProfileConfigurationData with capture period only must not contain capture objects: "
                + definableLoadProfileConfigurationData)
        .isFalse();
    assertThat(definableLoadProfileConfigurationData.hasCapturePeriod())
        .as(
            "DefinableLoadProfileConfigurationData with capture period only must contain capture period: "
                + definableLoadProfileConfigurationData)
        .isTrue();
    assertThat(definableLoadProfileConfigurationData.getCapturePeriod())
        .as("DefinableLoadProfileConfigurationData capture period")
        .isEqualTo(CAPTURE_PERIOD);
  }
}
