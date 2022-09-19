/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDateFormat.Field;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SynchronizeTimeCommandExecutorTest {

  private static final ObisCode LOGICAL_NAME = new ObisCode("0.0.1.0.0.255");

  private final DlmsDevice DLMS_DEVICE = new DlmsDevice();

  @Captor ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private MessageMetadata messageMetadata;

  private SynchronizeTimeCommandExecutor executor;

  @BeforeEach
  void setUp() {
    this.executor = new SynchronizeTimeCommandExecutor(new DlmsHelper());
  }

  @Test
  void testSynchronizeTime() throws ProtocolAdapterException, IOException {
    final String timeZone = "Europe/Amsterdam";
    final ZonedDateTime expectedTime = ZonedDateTime.now(TimeZone.getTimeZone(timeZone).toZoneId());

    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);

    final SynchronizeTimeRequestDto synchronizeTimeRequest =
        new SynchronizeTimeRequestDto(timeZone);

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(
            this.conn, this.DLMS_DEVICE, synchronizeTimeRequest, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(1)).set(this.setParameterArgumentCaptor.capture());

    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    assertThat(setParameters).hasSize(1);

    final SetParameter setParameter = setParameters.get(0);
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(InterfaceClass.CLOCK.id());
    assertThat(attributeAddress.getInstanceId()).isEqualTo(LOGICAL_NAME);
    assertThat(attributeAddress.getId()).isEqualTo(ClockAttribute.TIME.attributeId());

    assertThat(setParameter.getData().getType().name()).isEqualTo("DATE_TIME");
    final CosemDateTime cosemDateTime = setParameter.getData().getValue();

    assertThat(cosemDateTime.get(Field.HOUR)).isEqualTo(expectedTime.getHour());
    assertThat(cosemDateTime.get(Field.DEVIATION))
        .isEqualTo(expectedTime.getOffset().getTotalSeconds() / -60);

    final ZonedDateTime dateTime =
        ZonedDateTime.of(
            cosemDateTime.get(Field.YEAR),
            cosemDateTime.get(Field.MONTH),
            cosemDateTime.get(Field.DAY_OF_MONTH),
            cosemDateTime.get(Field.HOUR),
            cosemDateTime.get(Field.MINUTE),
            cosemDateTime.get(Field.SECOND),
            cosemDateTime.get(Field.HUNDREDTHS) * 10 * 1000,
            ZoneId.of(timeZone));
    assertThat(ChronoUnit.SECONDS.between(expectedTime, dateTime)).isZero();
  }
}
