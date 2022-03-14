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

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObisCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObjectDefinition;

public class PushSetupAlarmMappingTest {

  private static final String HOST = "host";
  private static final BigInteger PORT = BigInteger.TEN;
  private static final String DESTINATION = "host:10";
  private static final int PUSH_OBJECT_CLASS_ID = 1;
  private static final CosemObisCode PUSH_OBJECT_OBIS_CODE = new CosemObisCode(1, 2, 3, 4, 5, 6);
  private static final int PUSH_OBJECT_ATTRIBUTE_ID = 2;
  private static final int PUSH_OBJECT_DATA_INDEX = 3;
  private static final List<PushObject> PUSH_OBJECT_LIST = createPushObjectList();
  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  /** Tests if mapping a PushSetupAlarm object succeeds. */
  @Test
  public void testPushSetupAlarmMapping() {

    // build test data
    final PushSetupAlarm pushSetupAlarmOriginal = new PushSetupAlarm();
    pushSetupAlarmOriginal.setHost(HOST);
    pushSetupAlarmOriginal.setPort(PORT);
    pushSetupAlarmOriginal.getPushObjectList().addAll(PUSH_OBJECT_LIST);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm
        pushSetupAlarmMapped =
            this.configurationMapper.map(
                pushSetupAlarmOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm
                    .class);

    assertThat(pushSetupAlarmMapped).isNotNull();

    // port and host are combined into destination. The converter sets null values for the other
    // two variables of a SendDestinationAndMethod.
    assertThat(pushSetupAlarmMapped.getSendDestinationAndMethod().getDestination())
        .isEqualTo(DESTINATION);
    assertThat(pushSetupAlarmMapped.getSendDestinationAndMethod().getTransportService()).isNull();
    assertThat(pushSetupAlarmMapped.getSendDestinationAndMethod().getMessage()).isNull();

    // check mapping of PushObjectList
    assertThat(pushSetupAlarmMapped.getPushObjectList()).hasSize(1);
    final CosemObjectDefinition pushObject = pushSetupAlarmMapped.getPushObjectList().get(0);
    assertThat(pushObject.getClassId()).isEqualTo(PUSH_OBJECT_CLASS_ID);
    assertThat(pushObject.getLogicalName()).isEqualToComparingFieldByField(PUSH_OBJECT_OBIS_CODE);
    assertThat(pushObject.getAttributeIndex()).isEqualTo(PUSH_OBJECT_ATTRIBUTE_ID);
    assertThat(pushObject.getDataIndex()).isEqualTo(PUSH_OBJECT_DATA_INDEX);

    // Only SendDestinationAndMethod and PushObjectList are mapped:
    assertThat(pushSetupAlarmMapped.getLogicalName()).isNull();
    assertThat(pushSetupAlarmMapped.getCommunicationWindow()).isNull();
    assertThat(pushSetupAlarmMapped.getNumberOfRetries()).isNull();
    assertThat(pushSetupAlarmMapped.getRandomisationStartInterval()).isNull();
    assertThat(pushSetupAlarmMapped.getRepetitionDelay()).isNull();
  }

  private static List<PushObject> createPushObjectList() {
    final PushObject pushObject = new PushObject();
    final ObisCodeValues obisCode = new ObisCodeValues();
    obisCode.setA((short) PUSH_OBJECT_OBIS_CODE.getA());
    obisCode.setB((short) PUSH_OBJECT_OBIS_CODE.getB());
    obisCode.setC((short) PUSH_OBJECT_OBIS_CODE.getC());
    obisCode.setD((short) PUSH_OBJECT_OBIS_CODE.getD());
    obisCode.setE((short) PUSH_OBJECT_OBIS_CODE.getE());
    obisCode.setF((short) PUSH_OBJECT_OBIS_CODE.getF());
    pushObject.setClassId(PUSH_OBJECT_CLASS_ID);
    pushObject.setLogicalName(obisCode);
    pushObject.setAttributeIndex((byte) PUSH_OBJECT_ATTRIBUTE_ID);
    pushObject.setDataIndex(PUSH_OBJECT_DATA_INDEX);

    return Collections.singletonList(pushObject);
  }
}
