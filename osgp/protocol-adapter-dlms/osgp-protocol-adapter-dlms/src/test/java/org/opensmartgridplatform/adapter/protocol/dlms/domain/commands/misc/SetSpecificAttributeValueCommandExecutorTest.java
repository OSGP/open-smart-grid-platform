// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetSpecificAttributeValueRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SetSpecificAttributeValueCommandExecutorTest {

  private SetSpecificAttributeValueCommandExecutor executor;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  @Captor private ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  private static final String WATCHDOG_OBJECT_TYPE = "WATCHDOG_TIMER";
  private static final String WATCHDOG_TIMER_OBIS = "0.1.94.31.2.255";
  private static final int CLASS_ID = 1;
  private static final int ATTRIBUTE_ID = 2;
  private static final int VALUE = 25;

  @BeforeEach
  void setUp() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    this.executor = new SetSpecificAttributeValueCommandExecutor(objectConfigService);
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_2_2", "OTHER_PROTOCOL"},
      mode = EnumSource.Mode.EXCLUDE)
  void testExecute(final Protocol protocol) throws Exception {

    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(protocol);
    testDevice.setWithListMax(1);

    final SetSpecificAttributeValueRequestDto requestData =
        new SetSpecificAttributeValueRequestDto(WATCHDOG_OBJECT_TYPE, ATTRIBUTE_ID, VALUE);

    final AttributeAddress attributeAddress =
        new AttributeAddress(CLASS_ID, this.WATCHDOG_TIMER_OBIS, ATTRIBUTE_ID, null);
    final SetParameter expectedSetParameter =
        new SetParameter(attributeAddress, DataObject.newUInteger16Data(VALUE));

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS);

    // CALL
    this.executor.execute(
        this.connectionManager, testDevice, requestData, mock(MessageMetadata.class));

    // VERIFY
    assertThat(this.setParameterArgumentCaptor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedSetParameter);
  }

  @Test
  void testExecuteNoObject() {

    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(Protocol.DSMR_2_2);

    final SetSpecificAttributeValueRequestDto requestData =
        new SetSpecificAttributeValueRequestDto(WATCHDOG_OBJECT_TYPE, ATTRIBUTE_ID, VALUE);

    // CALL
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          this.executor.execute(
              this.connectionManager, testDevice, requestData, mock(MessageMetadata.class));
        });
  }
}
