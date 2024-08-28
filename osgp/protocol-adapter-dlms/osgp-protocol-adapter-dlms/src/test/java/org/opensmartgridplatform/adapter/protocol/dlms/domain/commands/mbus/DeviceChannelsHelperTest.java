// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.dlms.interfaceclass.method.MBusClientMethod.SLAVE_DEINSTALL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;

@ExtendWith(MockitoExtension.class)
public class DeviceChannelsHelperTest {

  private static final short PRIMARY_ADDRESS = 1;
  private static final long IDENTIFICATION_NUMBER_IN_BCD_AS_LONG = 287454020L;
  private static final String IDENTIFICATION_NUMBER_AS_STRING = "11223344";
  private static final int IDENTIFICATION_NUMBER_AS_VALUE = 11223344;
  private static final int MANUFACTURER_IDENTIFICATION = 1057;
  private static final String MANUFACTURER_IDENTIFICATION_AS_TEXT = "AAA";
  private static final short VERSION = 2;
  private static final short DEVICE_TYPE = 3;
  private static final String CALLING = DeviceChannelsHelper.class.getSimpleName();

  private final GetResult primaryAddress =
      new GetResultImpl(DataObject.newUInteger8Data(PRIMARY_ADDRESS));
  private final GetResult identificationNumberInBcd =
      new GetResultImpl(DataObject.newUInteger32Data(IDENTIFICATION_NUMBER_IN_BCD_AS_LONG));
  private final GetResult identificationNumber =
      new GetResultImpl(DataObject.newUInteger32Data(IDENTIFICATION_NUMBER_AS_VALUE));
  private final GetResult manufacturerIdentification =
      new GetResultImpl(DataObject.newUInteger16Data(MANUFACTURER_IDENTIFICATION));
  private final GetResult version = new GetResultImpl(DataObject.newUInteger8Data(VERSION));
  private final GetResult deviceType = new GetResultImpl(DataObject.newUInteger8Data(DEVICE_TYPE));

  DlmsHelper dlmsHelper = new DlmsHelper();

  @Mock DlmsConnectionManager conn;

  @Mock DlmsConnection dlmsConnection;

  @Mock DlmsDevice device;

  @Mock CosemObjectAccessor mBusSetup;

  private DeviceChannelsHelper deviceChannelsHelper;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @BeforeEach
  void setup() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final ObjectConfigServiceHelper objectConfigServiceHelper =
        new ObjectConfigServiceHelper(objectConfigService);
    this.deviceChannelsHelper =
        new DeviceChannelsHelper(this.dlmsHelper, objectConfigServiceHelper);
  }

  @Test
  void testDeinstallSlave() throws ProtocolAdapterException {

    // successful deinstall with signed integer parameter
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.mBusSetup.callMethod(eq(CALLING), eq(SLAVE_DEINSTALL), any()))
        .thenReturn(MethodResultCode.SUCCESS);

    final MethodResultCode methodResultCode =
        this.deviceChannelsHelper.deinstallSlave(this.conn, this.device, (short) 1, this.mBusSetup);

    assertThat(methodResultCode).isEqualTo(MethodResultCode.SUCCESS);

    verify(this.mBusSetup, times(1)).callMethod(eq(CALLING), eq(SLAVE_DEINSTALL), any());
  }

  @Test
  void testDeinstallSlave2() throws ProtocolAdapterException {

    // successful deinstall with unsigned integer parameter after
    // unsuccessful
    // attempt with signed integer (TYPE_UNMATCHED)
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.mBusSetup.callMethod(eq(CALLING), eq(SLAVE_DEINSTALL), any(DataObject.class)))
        .thenReturn(MethodResultCode.TYPE_UNMATCHED)
        .thenReturn(MethodResultCode.SUCCESS);

    final MethodResultCode methodResultCode =
        this.deviceChannelsHelper.deinstallSlave(this.conn, this.device, (short) 1, this.mBusSetup);

    assertThat(methodResultCode).isEqualTo(MethodResultCode.SUCCESS);

    verify(this.mBusSetup, times(2)).callMethod(eq(CALLING), eq(SLAVE_DEINSTALL), any());
  }

  @Test
  void testDeinstallSlave3() throws ProtocolAdapterException {

    // unsuccessful deinstall with signed integer parameter
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.mBusSetup.callMethod(eq(CALLING), eq(SLAVE_DEINSTALL), any(DataObject.class)))
        .thenReturn(MethodResultCode.SCOPE_OF_ACCESS_VIOLATION);

    final MethodResultCode methodResultCode =
        this.deviceChannelsHelper.deinstallSlave(this.conn, this.device, (short) 1, this.mBusSetup);

    assertThat(methodResultCode).isEqualTo(MethodResultCode.SCOPE_OF_ACCESS_VIOLATION);

    verify(this.mBusSetup, times(1)).callMethod(eq(CALLING), eq(SLAVE_DEINSTALL), any());
  }

  @Test
  void testDeinstallSlave4() throws ProtocolAdapterException {

    // unsuccessful deinstall with unsigned integer parameter (second
    // attempt)

    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.mBusSetup.callMethod(eq(CALLING), eq(SLAVE_DEINSTALL), any(DataObject.class)))
        .thenReturn(MethodResultCode.TYPE_UNMATCHED)
        .thenReturn(MethodResultCode.SCOPE_OF_ACCESS_VIOLATION);

    final MethodResultCode methodResultCode =
        this.deviceChannelsHelper.deinstallSlave(this.conn, this.device, (short) 1, this.mBusSetup);

    assertThat(methodResultCode).isEqualTo(MethodResultCode.SCOPE_OF_ACCESS_VIOLATION);

    verify(this.mBusSetup, times(2)).callMethod(eq(CALLING), eq(SLAVE_DEINSTALL), any());
  }

  @Test
  void testGetChannelElementValuesDsmr4() throws Exception {
    final List<GetResult> resultList =
        new ArrayList<>(
            Arrays.asList(
                this.primaryAddress,
                this.identificationNumberInBcd,
                this.manufacturerIdentification,
                this.version,
                this.deviceType));
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(anyList())).thenReturn(resultList);
    when(this.device.getProtocolName()).thenReturn("DSMR");
    when(this.device.getProtocolVersion()).thenReturn("4.2.2");

    final ChannelElementValuesDto values =
        this.deviceChannelsHelper.getChannelElementValues(this.conn, this.device, (short) 1);

    assertThat(values.getPrimaryAddress()).isEqualTo(PRIMARY_ADDRESS);
    assertThat(values.getIdentificationNumber()).isEqualTo(IDENTIFICATION_NUMBER_AS_STRING);
    assertThat(values.getManufacturerIdentification())
        .isEqualTo(MANUFACTURER_IDENTIFICATION_AS_TEXT);
    assertThat(values.getVersion()).isEqualTo(VERSION);
    assertThat(values.getDeviceTypeIdentification()).isEqualTo(DEVICE_TYPE);
  }

  @Test
  void testGetChannelElementValuesSmr5() throws Exception {
    final List<GetResult> resultList =
        new ArrayList<>(
            Arrays.asList(
                this.primaryAddress,
                this.identificationNumber,
                this.manufacturerIdentification,
                this.version,
                this.deviceType));
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(anyList())).thenReturn(resultList);
    when(this.device.getProtocolName()).thenReturn("SMR");
    when(this.device.getProtocolVersion()).thenReturn("5.1");

    final ChannelElementValuesDto values =
        this.deviceChannelsHelper.getChannelElementValues(this.conn, this.device, (short) 1);

    assertThat(values.getPrimaryAddress()).isEqualTo(PRIMARY_ADDRESS);
    assertThat(values.getIdentificationNumber()).isEqualTo(IDENTIFICATION_NUMBER_AS_STRING);
    assertThat(values.getManufacturerIdentification())
        .isEqualTo(MANUFACTURER_IDENTIFICATION_AS_TEXT);
    assertThat(values.getVersion()).isEqualTo(VERSION);
    assertThat(values.getDeviceTypeIdentification()).isEqualTo(DEVICE_TYPE);
  }

  @Test
  void testGetChannelElementValuesInvalidManufacturerId() throws Exception {

    final GetResult manufacturerIdentificationInvalid =
        new GetResultImpl(DataObject.newUInteger16Data(123));

    final List<GetResult> resultList =
        new ArrayList<>(
            Arrays.asList(
                this.primaryAddress,
                this.identificationNumberInBcd,
                manufacturerIdentificationInvalid,
                this.version,
                this.deviceType));
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(anyList())).thenReturn(resultList);
    when(this.device.getProtocolName()).thenReturn("SMR");
    when(this.device.getProtocolVersion()).thenReturn("5.0.0");

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              this.deviceChannelsHelper.getChannelElementValues(this.conn, this.device, (short) 1);
            });
  }

  @Test
  void testGetChannelElementValuesIdenfiticationNumberNull() throws Exception {

    final GetResult identificationNumberNull = new GetResultImpl(null);

    final List<GetResult> resultList =
        new ArrayList<>(
            Arrays.asList(
                this.primaryAddress,
                identificationNumberNull,
                this.manufacturerIdentification,
                this.version,
                this.deviceType));
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(anyList())).thenReturn(resultList);
    when(this.device.getProtocolName()).thenReturn("SMR");
    when(this.device.getProtocolVersion()).thenReturn("5.0.0");

    final ChannelElementValuesDto values =
        this.deviceChannelsHelper.getChannelElementValues(this.conn, this.device, (short) 1);

    assertThat(values.getIdentificationNumber()).isNull();
  }

  @Test
  void testGetChannelElementValuesIdenfiticationNumberInvalid() throws Exception {

    final GetResult identificationNumberInvalid =
        new GetResultImpl(DataObject.newUInteger32Data(3147483648L));

    final List<GetResult> resultList =
        new ArrayList<>(
            Arrays.asList(
                this.primaryAddress,
                identificationNumberInvalid,
                this.manufacturerIdentification,
                this.version,
                this.deviceType));
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(anyList())).thenReturn(resultList);
    when(this.device.getProtocolName()).thenReturn("SMR");
    when(this.device.getProtocolVersion()).thenReturn("5.0.0");

    final InvalidIdentificationNumberException exception =
        assertThrows(
            InvalidIdentificationNumberException.class,
            () ->
                this.deviceChannelsHelper.getChannelElementValues(
                    this.conn, this.device, (short) 1));

    assertThat(exception.getChannelElementValuesDto().getIdentificationNumber())
        .isEqualTo("DOUBLE_LONG_UNSIGNED Value: 3147483648");
  }

  @Test
  void testFindEmptyChannelWhenNoEmptyChannel() {

    final ChannelElementValuesDto result1 =
        this.deviceChannelsHelper.findEmptyChannel(Collections.emptyList());
    assertThat(result1).isNull();

    final ChannelElementValuesDto nonEmptyChannel =
        new ChannelElementValuesDto((short) 1, (short) 0, null, "id", (short) 0, (short) 0);
    final ChannelElementValuesDto result2 =
        this.deviceChannelsHelper.findEmptyChannel(Collections.singletonList(nonEmptyChannel));
    assertThat(result2).isNull();
  }

  @Test
  void testFindEmptyChannel() {

    final ChannelElementValuesDto emptyChannel =
        new ChannelElementValuesDto((short) 1, (short) 0, "00000000", null, (short) 0, (short) 0);
    final ChannelElementValuesDto nonEmptyChannel =
        new ChannelElementValuesDto((short) 2, (short) 0, "12345678", null, (short) 0, (short) 0);

    final ChannelElementValuesDto result =
        this.deviceChannelsHelper.findEmptyChannel(Arrays.asList(nonEmptyChannel, emptyChannel));
    assertThat(result).isEqualTo(emptyChannel);
  }
}
