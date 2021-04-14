/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.interfaceclass.method.MBusClientMethod;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class DeviceChannelsHelperTest {

  private static final String OBIS_CODE_MBUS_CHANNEL_ONE = "0.1.24.1.0.255";
  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final DeviceChannelsHelper deviceChannelsHelper =
      new DeviceChannelsHelper(this.dlmsHelper);

  private static final short PRIMARY_ADDRESS = 1;
  private static final long IDENTIFICATION_NUMBER = 16137489L;
  private static final int MANUFACTURER_IDENTIFICATION = 1057;
  private static final String MANUFACTURER_IDENTIFICATION_AS_TEXT = "AAA";
  private static final short VERSION = 2;
  private static final short DEVICE_TYPE = 3;

  private final GetResult primaryAddress =
      new GetResultImpl(DataObject.newUInteger8Data(PRIMARY_ADDRESS));
  private final GetResult identificationNumber =
      new GetResultImpl(DataObject.newUInteger32Data(IDENTIFICATION_NUMBER));
  private final GetResult manufacturerIdentification =
      new GetResultImpl(DataObject.newUInteger16Data(MANUFACTURER_IDENTIFICATION));
  private final GetResult version = new GetResultImpl(DataObject.newUInteger8Data(VERSION));
  private final GetResult deviceType = new GetResultImpl(DataObject.newUInteger8Data(DEVICE_TYPE));

  @Mock DlmsConnectionManager conn;

  @Mock Logger log;

  @Mock DlmsDevice device;

  @Mock CosemObjectAccessor mBusSetup;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Test
  void testGetObisCode() {
    final int channel = 1;
    final ObisCode obisCode = this.deviceChannelsHelper.getObisCode(channel);
    assertThat(obisCode.asDecimalString()).isEqualTo(OBIS_CODE_MBUS_CHANNEL_ONE);
  }

  @Test
  void testDeinstallSlave() throws ProtocolAdapterException {

    // successful deinstall with signed integer parameter
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.mBusSetup.callMethod(eq(MBusClientMethod.SLAVE_DEINSTALL), any()))
        .thenReturn(MethodResultCode.SUCCESS);

    final MethodResultCode methodResultCode =
        this.deviceChannelsHelper.deinstallSlave(this.conn, this.device, (short) 1, this.mBusSetup);

    assertThat(methodResultCode).isEqualTo(MethodResultCode.SUCCESS);

    verify(this.mBusSetup, times(1)).callMethod(eq(MBusClientMethod.SLAVE_DEINSTALL), any());
  }

  @Test
  void testDeinstallSlave2() throws ProtocolAdapterException {

    // successful deinstall with unsigned integer parameter after
    // unsuccessful
    // attempt with signed integer (TYPE_UNMATCHED)
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.mBusSetup.callMethod(eq(MBusClientMethod.SLAVE_DEINSTALL), any(DataObject.class)))
        .thenReturn(MethodResultCode.TYPE_UNMATCHED)
        .thenReturn(MethodResultCode.SUCCESS);

    final MethodResultCode methodResultCode =
        this.deviceChannelsHelper.deinstallSlave(this.conn, this.device, (short) 1, this.mBusSetup);

    assertThat(methodResultCode).isEqualTo(MethodResultCode.SUCCESS);

    verify(this.mBusSetup, times(2)).callMethod(eq(MBusClientMethod.SLAVE_DEINSTALL), any());
  }

  @Test
  void testDeinstallSlave3() throws ProtocolAdapterException {

    // unsuccesful deinstall with signed integer parameter
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.mBusSetup.callMethod(eq(MBusClientMethod.SLAVE_DEINSTALL), any(DataObject.class)))
        .thenReturn(MethodResultCode.SCOPE_OF_ACCESS_VIOLATION);

    final MethodResultCode methodResultCode =
        this.deviceChannelsHelper.deinstallSlave(this.conn, this.device, (short) 1, this.mBusSetup);

    assertThat(methodResultCode).isEqualTo(MethodResultCode.SCOPE_OF_ACCESS_VIOLATION);

    verify(this.mBusSetup, times(1)).callMethod(eq(MBusClientMethod.SLAVE_DEINSTALL), any());
  }

  @Test
  void testDeinstallSlave4() throws ProtocolAdapterException {

    // unsuccesful deinstall with unsigned integer parameter (second
    // attempt)

    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.mBusSetup.callMethod(eq(MBusClientMethod.SLAVE_DEINSTALL), any(DataObject.class)))
        .thenReturn(MethodResultCode.TYPE_UNMATCHED)
        .thenReturn(MethodResultCode.SCOPE_OF_ACCESS_VIOLATION);

    final MethodResultCode methodResultCode =
        this.deviceChannelsHelper.deinstallSlave(this.conn, this.device, (short) 1, this.mBusSetup);

    assertThat(methodResultCode).isEqualTo(MethodResultCode.SCOPE_OF_ACCESS_VIOLATION);

    verify(this.mBusSetup, times(2)).callMethod(eq(MBusClientMethod.SLAVE_DEINSTALL), any());
  }

  @Test
  void testMakeChannelElementValues() throws Exception {
    final List<GetResult> resultList =
        new ArrayList<>(
            Arrays.asList(
                this.primaryAddress,
                this.identificationNumber,
                this.manufacturerIdentification,
                this.version,
                this.deviceType));

    final ChannelElementValuesDto values =
        this.deviceChannelsHelper.makeChannelElementValues((short) 1, resultList);

    assertThat(values.getPrimaryAddress()).isEqualTo(PRIMARY_ADDRESS);
    assertThat(values.getIdentificationNumber()).isEqualTo(String.valueOf(IDENTIFICATION_NUMBER));
    assertThat(values.getManufacturerIdentification())
        .isEqualTo(MANUFACTURER_IDENTIFICATION_AS_TEXT);
    assertThat(values.getVersion()).isEqualTo(VERSION);
    assertThat(values.getDeviceTypeIdentification()).isEqualTo(DEVICE_TYPE);
  }

  @Test
  void testMakeChannelElementValuesInvalidManufacturerId() throws Exception {

    final GetResult manufacturerIdentificationInvalid =
        new GetResultImpl(DataObject.newUInteger16Data(123));

    final List<GetResult> resultList =
        new ArrayList<>(
            Arrays.asList(
                this.primaryAddress,
                this.identificationNumber,
                manufacturerIdentificationInvalid,
                this.version,
                this.deviceType));

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              this.deviceChannelsHelper.makeChannelElementValues((short) 1, resultList);
            });
  }

  @Test
  public void testMakeChannelElementValuesIdenfiticationNumberNull() throws Exception {

    final GetResult identificationNumberNull = new GetResultImpl(null);

    final List<GetResult> resultList =
        new ArrayList<>(
            Arrays.asList(
                this.primaryAddress,
                identificationNumberNull,
                this.manufacturerIdentification,
                this.version,
                this.deviceType));

    final ChannelElementValuesDto values =
        this.deviceChannelsHelper.makeChannelElementValues((short) 1, resultList);

    assertThat(values.getIdentificationNumber()).isEqualTo(null);
  }
}
