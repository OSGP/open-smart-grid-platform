// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmuc.jdlms.AccessResultCode.SUCCESS;
import static org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass.DATA;
import static org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass.REGISTER;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute.VALUE;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetThdFingerprintResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetThdFingerprintCommandExecutorTest {

  private GetThdFingerprintCommandExecutor executor;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final DlmsHelper dlmsHelper = new DlmsHelper();
    this.executor = new GetThdFingerprintCommandExecutor(objectConfigService, dlmsHelper);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void testExecute(final boolean polyphase) throws Exception {
    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(Protocol.SMR_5_2);
    testDevice.setPolyphase(polyphase);
    testDevice.setWithListMax(10);

    final List<GetResult> resultList = this.createGetResults(testDevice);

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(ArgumentMatchers.anyList())).thenReturn(resultList);

    final GetThdFingerprintResponseDto result =
        this.executor.execute(
            this.connectionManager, testDevice, null, mock(MessageMetadata.class));

    final List<AttributeAddress> expectedAttributeAddresses =
        createExpectedAttributeAddresses(testDevice);

    final ArgumentCaptor<List<AttributeAddress>> attributeAddressesCaptor =
        ArgumentCaptor.forClass(List.class);
    verify(this.dlmsConnection).get(attributeAddressesCaptor.capture());
    final List<AttributeAddress> capturedAttributeAddresses = attributeAddressesCaptor.getValue();
    assertThat(capturedAttributeAddresses).hasSize(expectedAttributeAddresses.size());
    assertThat(capturedAttributeAddresses)
        .usingRecursiveComparison()
        .isEqualTo(expectedAttributeAddresses);

    assertCapturedValues(result, testDevice);
  }

  private static void assertCapturedValues(
      final GetThdFingerprintResponseDto result, final DlmsDevice testDevice) {
    assertThat(result.getThdInstantaneousCurrentL1()).isEqualTo(1);
    assertThat(result.getThdInstantaneousCurrentFingerprintL1())
        .isEqualTo(IntStream.rangeClosed(101, 115).boxed().toList());
    assertThat(result.getThdCurrentOverLimitCounterL1()).isEqualTo(10);
    if (testDevice.isPolyphase()) {
      assertThat(result.getThdInstantaneousCurrentL2()).isEqualTo(2);
      assertThat(result.getThdInstantaneousCurrentL3()).isEqualTo(3);
      assertThat(result.getThdInstantaneousCurrentFingerprintL2())
          .isEqualTo(IntStream.rangeClosed(201, 215).boxed().toList());
      assertThat(result.getThdInstantaneousCurrentFingerprintL3())
          .isEqualTo(IntStream.rangeClosed(301, 315).boxed().toList());
      assertThat(result.getThdCurrentOverLimitCounterL2()).isEqualTo(20);
      assertThat(result.getThdCurrentOverLimitCounterL3()).isEqualTo(30);
    }
  }

  private static List<AttributeAddress> createExpectedAttributeAddresses(
      final DlmsDevice testDevice) {
    if (testDevice.isPolyphase()) {
      return List.of(
          new AttributeAddress(REGISTER.id(), "1.0.31.7.124.255", VALUE.attributeId()),
          new AttributeAddress(REGISTER.id(), "1.0.51.7.124.255", VALUE.attributeId()),
          new AttributeAddress(REGISTER.id(), "1.0.71.7.124.255", VALUE.attributeId()),
          new AttributeAddress(DATA.id(), "0.1.94.31.24.255", DataAttribute.VALUE.attributeId()),
          new AttributeAddress(DATA.id(), "0.1.94.31.25.255", DataAttribute.VALUE.attributeId()),
          new AttributeAddress(DATA.id(), "0.1.94.31.26.255", DataAttribute.VALUE.attributeId()),
          new AttributeAddress(REGISTER.id(), "1.0.31.36.124.255", VALUE.attributeId()),
          new AttributeAddress(REGISTER.id(), "1.0.51.36.124.255", VALUE.attributeId()),
          new AttributeAddress(REGISTER.id(), "1.0.71.36.124.255", VALUE.attributeId()));
    } else {
      return List.of(
          new AttributeAddress(REGISTER.id(), "1.0.31.7.124.255", VALUE.attributeId()),
          new AttributeAddress(DATA.id(), "0.1.94.31.24.255", DataAttribute.VALUE.attributeId()),
          new AttributeAddress(REGISTER.id(), "1.0.31.36.124.255", VALUE.attributeId()));
    }
  }

  private List<GetResult> createGetResults(final DlmsDevice device) {
    if (device.isPolyphase()) {
      return List.of(
          this.createValueResult(1, SUCCESS),
          this.createValueResult(2, SUCCESS),
          this.createValueResult(3, SUCCESS),
          this.createFingerprintResult(100, 15, SUCCESS),
          this.createFingerprintResult(200, 15, SUCCESS),
          this.createFingerprintResult(300, 15, SUCCESS),
          this.createValueResult(10, SUCCESS),
          this.createValueResult(20, SUCCESS),
          this.createValueResult(30, SUCCESS));
    } else {
      return List.of(
          this.createValueResult(1, SUCCESS),
          this.createFingerprintResult(100, 15, SUCCESS),
          this.createValueResult(10, SUCCESS));
    }
  }

  @Test
  void testExecuteNoObject() {

    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(Protocol.DSMR_2_2);

    // CALL
    assertThrows(
        NotSupportedByProtocolException.class,
        () -> {
          this.executor.execute(
              this.connectionManager, testDevice, null, mock(MessageMetadata.class));
        });
  }

  private GetResultImpl createValueResult(final int value, final AccessResultCode resultCode) {
    return new GetResultImpl(DataObject.newUInteger16Data(value), resultCode);
  }

  private GetResultImpl createFingerprintResult(
      final int startValue, final int amount, final AccessResultCode resultCode) {

    final List<DataObject> values =
        IntStream.rangeClosed(1, amount)
            .boxed()
            .map(i -> DataObject.newUInteger16Data(startValue + i))
            .toList();

    final DataObject fingerprintArray = DataObject.newArrayData(values);
    return new GetResultImpl(fingerprintArray, resultCode);
  }
}
