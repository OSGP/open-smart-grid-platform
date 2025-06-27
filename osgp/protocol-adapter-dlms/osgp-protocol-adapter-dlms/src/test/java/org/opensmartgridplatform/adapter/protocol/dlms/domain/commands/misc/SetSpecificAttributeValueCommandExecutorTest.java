// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetSpecificAttributeValueRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ValueToSetDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SetSpecificAttributeValueCommandExecutorTest {

  private SetSpecificAttributeValueCommandExecutor executor;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  @Captor private ArgumentCaptor<List<SetParameter>> setParameterArgumentCaptor;

  private static final String WATCHDOG_OBJECT_TYPE = "WATCHDOG_TIMER";
  private static final String WATCHDOG_OBIS = "0.1.94.31.2.255";
  private static final int CLASS_ID = 1;
  private static final int ATTRIBUTE_ID = 2;
  private static final int VALUE = 25;
  private static final String THRESHOLD_OBJECT_TYPE = "THRESHOLD_VOLTAGE_SWELL";
  private static final String THRESHOLD_OBIS = "1.0.12.35.0.255";
  private static final int CLASS_ID_THRESHOLD = 3;
  private static final int ATTRIBUTE_ID_THRESHOLD = 2;
  private static final int VALUE_THRESHOLD = 100;

  @BeforeEach
  void setUp() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final DlmsHelper dlmsHelper = new DlmsHelper();
    this.executor = new SetSpecificAttributeValueCommandExecutor(objectConfigService, dlmsHelper);
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_2_2", "OTHER_PROTOCOL"},
      mode = EnumSource.Mode.EXCLUDE)
  void testExecuteOneObject(final Protocol protocol) throws Exception {

    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(protocol);
    testDevice.setWithListMax(1);

    final SetSpecificAttributeValueRequestDto requestData =
        new SetSpecificAttributeValueRequestDto(
            List.of(new ValueToSetDto(WATCHDOG_OBJECT_TYPE, ATTRIBUTE_ID, VALUE)));

    final AttributeAddress attributeAddress =
        new AttributeAddress(CLASS_ID, this.WATCHDOG_OBIS, ATTRIBUTE_ID, null);
    final SetParameter expectedSetParameter =
        new SetParameter(attributeAddress, DataObject.newUInteger16Data(VALUE));

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(List.of(AccessResultCode.SUCCESS));

    // CALL
    this.executor.execute(
        this.connectionManager, testDevice, requestData, mock(MessageMetadata.class));

    // VERIFY
    assertThat(this.setParameterArgumentCaptor.getValue().get(0))
        .usingRecursiveComparison()
        .isEqualTo(expectedSetParameter);
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_2_2", "OTHER_PROTOCOL"},
      mode = EnumSource.Mode.EXCLUDE)
  void testExecuteMultipleObjects(final Protocol protocol) throws Exception {

    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(protocol);
    testDevice.setWithListMax(2);

    final SetSpecificAttributeValueRequestDto requestData =
        new SetSpecificAttributeValueRequestDto(
            List.of(
                new ValueToSetDto(WATCHDOG_OBJECT_TYPE, ATTRIBUTE_ID, VALUE),
                new ValueToSetDto(THRESHOLD_OBJECT_TYPE, ATTRIBUTE_ID_THRESHOLD, VALUE_THRESHOLD)));

    final AttributeAddress attributeAddressWatchdog =
        new AttributeAddress(CLASS_ID, this.WATCHDOG_OBIS, ATTRIBUTE_ID, null);
    final SetParameter expectedSetParameterWatchdog =
        new SetParameter(attributeAddressWatchdog, DataObject.newUInteger16Data(VALUE));

    final AttributeAddress attributeAddressThreshold =
        new AttributeAddress(CLASS_ID_THRESHOLD, this.THRESHOLD_OBIS, ATTRIBUTE_ID_THRESHOLD, null);
    final SetParameter expectedSetParameterThreshold =
        new SetParameter(attributeAddressThreshold, DataObject.newUInteger16Data(VALUE_THRESHOLD));

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(List.of(AccessResultCode.SUCCESS));

    // CALL
    this.executor.execute(
        this.connectionManager, testDevice, requestData, mock(MessageMetadata.class));

    // VERIFY
    assertThat(this.setParameterArgumentCaptor.getValue().get(0))
        .usingRecursiveComparison()
        .isEqualTo(expectedSetParameterWatchdog);
    assertThat(this.setParameterArgumentCaptor.getValue().get(1))
        .usingRecursiveComparison()
        .isEqualTo(expectedSetParameterThreshold);
  }

  @Test
  void testExecuteNoObject() {

    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(Protocol.DSMR_2_2);

    final SetSpecificAttributeValueRequestDto requestData =
        new SetSpecificAttributeValueRequestDto(
            List.of(new ValueToSetDto(WATCHDOG_OBJECT_TYPE, ATTRIBUTE_ID, VALUE)));

    // CALL
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          this.executor.execute(
              this.connectionManager, testDevice, requestData, mock(MessageMetadata.class));
        });
  }
}
