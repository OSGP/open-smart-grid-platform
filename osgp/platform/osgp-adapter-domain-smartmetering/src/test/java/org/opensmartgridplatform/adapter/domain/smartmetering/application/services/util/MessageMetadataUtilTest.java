/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.utils.MessageMetadataUtil;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class MessageMetadataUtilTest {

  private static final String LOCAL_HOST = "localhost";

  private static final Integer BTS_ID = 580;

  private static final Integer CELL_ID = 1;

  private static final String DEVICE_MODEL_CODE = "G4";

  private static final String DEVICE_IDENTIFICATION = "TEST1024000000001";

  @Mock private SmartMeter smartMeter;

  @Mock private DeviceModel deviceModel;

  @Test
  void doWithDeviceModelCode() {
    when(this.deviceModel.getModelCode()).thenReturn(DEVICE_MODEL_CODE);
    when(this.smartMeter.getDeviceIdentification()).thenReturn(DEVICE_IDENTIFICATION);
    when(this.smartMeter.getNetworkAddress()).thenReturn(LOCAL_HOST);
    when(this.smartMeter.getBtsId()).thenReturn(BTS_ID);
    when(this.smartMeter.getCellId()).thenReturn(CELL_ID);
    when(this.smartMeter.getDeviceModel()).thenReturn(this.deviceModel);

    final MessageMetadata messageMetadata =
        MessageMetadataUtil.buildMetadata(MessageMetadata.newBuilder().build(), this.smartMeter);

    assertEquals(DEVICE_IDENTIFICATION, messageMetadata.getDeviceIdentification());
    assertEquals(LOCAL_HOST, messageMetadata.getNetworkAddress());
    assertEquals(BTS_ID, messageMetadata.getBaseTransceiverStationId());
    assertEquals(CELL_ID, messageMetadata.getCellId());
    assertEquals(DEVICE_MODEL_CODE, messageMetadata.getDeviceModelCode());
  }

  @Test
  void doWithoutDeviceModelCode() {
    when(this.smartMeter.getDeviceIdentification()).thenReturn(DEVICE_IDENTIFICATION);
    when(this.smartMeter.getNetworkAddress()).thenReturn(LOCAL_HOST);
    when(this.smartMeter.getBtsId()).thenReturn(BTS_ID);
    when(this.smartMeter.getCellId()).thenReturn(CELL_ID);
    when(this.smartMeter.getDeviceModel()).thenReturn(null);

    final MessageMetadata messageMetadata =
        MessageMetadataUtil.buildMetadata(MessageMetadata.newBuilder().build(), this.smartMeter);

    assertEquals(DEVICE_IDENTIFICATION, messageMetadata.getDeviceIdentification());
    assertEquals(LOCAL_HOST, messageMetadata.getNetworkAddress());
    assertEquals(BTS_ID, messageMetadata.getBaseTransceiverStationId());
    assertEquals(CELL_ID, messageMetadata.getCellId());
    assertNull(messageMetadata.getDeviceModelCode());
  }
}
