// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.CommunicationMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BitErrorRateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CircuitSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemRegistrationStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PacketSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SignalQualityDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetGsmDiagnosticCommandExecutorTest {

  private GetGsmDiagnosticCommandExecutor executor;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsHelper dlmsHelper;

  @Mock private DlmsConnectionManager connectionManager;

  private ObjectConfigService objectConfigService;

  private final DlmsDevice device = this.createDevice(Protocol.SMR_5_1, CommunicationMethod.CDMA);
  private final int classId = InterfaceClass.GSM_DIAGNOSTIC.id();
  private final String obisCode = "0.1.25.6.0.255";
  private final GetGsmDiagnosticRequestDto request = new GetGsmDiagnosticRequestDto();
  private MessageMetadata messageMetadata;

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    this.objectConfigService = new ObjectConfigService(null);
    this.executor = new GetGsmDiagnosticCommandExecutor(this.dlmsHelper, this.objectConfigService);

    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
  }

  @Test
  void testExecuteObjectNotFound() throws ProtocolAdapterException {

    // CALL
    try {
      this.executor.execute(
          this.connectionManager,
          this.createDevice(Protocol.SMR_4_3, CommunicationMethod.LTE),
          this.request,
          this.messageMetadata);
      fail("When no matching object is found, then execute should fail");
    } catch (final IllegalArgumentException e) {
      assertThat(e.getMessage())
          .isEqualTo("No object found of type LTE_DIAGNOSTIC in profile SMR version 4.3");
    }
  }

  @Test
  void testHappy() throws Exception {

    // SETUP - mock return data objects
    final GetResult result2 = mock(GetResult.class);
    final GetResult result3 = mock(GetResult.class);
    final GetResult result4 = mock(GetResult.class);
    final GetResult result5 = mock(GetResult.class);
    final GetResult result6 = mock(GetResult.class);
    final GetResult result7 = mock(GetResult.class);
    when(result2.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(result3.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(result4.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(result5.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(result6.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(result7.getResultCode()).thenReturn(AccessResultCode.SUCCESS);

    final DataObject operator = mock(DataObject.class);
    when(result2.getResultData()).thenReturn(operator);
    when(operator.getValue()).thenReturn(new byte[] {65, 66});

    final DataObject modemRegistrationStatus = mock(DataObject.class);
    when(result3.getResultData()).thenReturn(modemRegistrationStatus);
    when(modemRegistrationStatus.getValue()).thenReturn(2);

    final DataObject csStatus = mock(DataObject.class);
    when(result4.getResultData()).thenReturn(csStatus);
    when(csStatus.getValue()).thenReturn(3);

    final DataObject psStatus = mock(DataObject.class);
    when(result5.getResultData()).thenReturn(psStatus);
    when(psStatus.getValue()).thenReturn(4);

    final DataObject cellInfo = mock(DataObject.class);
    when(result6.getResultData()).thenReturn(cellInfo);
    final DataObject cellId = mock(DataObject.class);
    final DataObject locationId = mock(DataObject.class);
    final DataObject signalQuality = mock(DataObject.class);
    final DataObject bitErrorRate = mock(DataObject.class);
    final DataObject mcc = mock(DataObject.class);
    final DataObject mnc = mock(DataObject.class);
    final DataObject channelNumber = mock(DataObject.class);
    when(cellInfo.getValue())
        .thenReturn(
            Arrays.asList(
                cellId, locationId, signalQuality, bitErrorRate, mcc, mnc, channelNumber));
    when(cellId.getValue()).thenReturn(128L);
    when(locationId.getValue()).thenReturn(1);
    when(signalQuality.getValue()).thenReturn((short) 2);
    when(bitErrorRate.getValue()).thenReturn((short) 3);
    when(mcc.getValue()).thenReturn(4);
    when(mnc.getValue()).thenReturn(5);
    when(channelNumber.getValue()).thenReturn(6L);

    final DataObject adjacentCells = mock(DataObject.class);
    when(result7.getResultData()).thenReturn(adjacentCells);
    final DataObject adjacentCell = mock(DataObject.class);
    when(adjacentCells.getValue()).thenReturn(Collections.singletonList(adjacentCell));
    final DataObject adjacentCellId = mock(DataObject.class);
    final DataObject adjacentCellSignalQuality = mock(DataObject.class);
    when(adjacentCell.getValue())
        .thenReturn(Arrays.asList(adjacentCellId, adjacentCellSignalQuality));
    when(adjacentCellId.getValue()).thenReturn(256L);
    when(adjacentCellSignalQuality.getValue()).thenReturn((short) 7);

    when(this.dlmsHelper.getAndCheck(
            eq(this.connectionManager), eq(this.device), eq("Get GsmDiagnostic"), any()))
        .thenReturn(Arrays.asList(result2, result3, result4, result5, result6, result7));

    // CALL
    final GetGsmDiagnosticResponseDto result =
        this.executor.execute(
            this.connectionManager, this.device, this.request, this.messageMetadata);

    // VERIFY calls to mocks
    verify(this.dlmsMessageListener)
        .setDescription(
            String.format(
                "Get GsmDiagnostic, retrieve attributes: %s, %s, %s, %s, %s, %s",
                this.createAttributeAddress(2),
                this.createAttributeAddress(3),
                this.createAttributeAddress(4),
                this.createAttributeAddress(5),
                this.createAttributeAddress(6),
                this.createAttributeAddress(7)));

    // VERIFY contents of the return value
    assertThat(result.getOperator()).isEqualTo("AB");
    assertThat(result.getModemRegistrationStatus())
        .isEqualTo(ModemRegistrationStatusDto.fromIndexValue(2));
    assertThat(result.getCircuitSwitchedStatus())
        .isEqualTo(CircuitSwitchedStatusDto.fromIndexValue(3));
    assertThat(result.getPacketSwitchedStatus())
        .isEqualTo(PacketSwitchedStatusDto.fromIndexValue(4));
    assertThat(result.getCellInfo().getCellId()).isEqualTo(128L);
    assertThat(result.getCellInfo().getLocationId()).isEqualTo(1);
    assertThat(result.getCellInfo().getSignalQuality())
        .isEqualTo(SignalQualityDto.fromIndexValue(2));
    assertThat(result.getCellInfo().getBitErrorRate()).isEqualTo(BitErrorRateDto.fromIndexValue(3));
    assertThat(result.getCellInfo().getMobileCountryCode()).isEqualTo(4);
    assertThat(result.getCellInfo().getMobileNetworkCode()).isEqualTo(5);
    assertThat(result.getCellInfo().getChannelNumber()).isEqualTo(6);
    assertThat(result.getAdjacentCells()).hasSize(1);
    assertThat(result.getAdjacentCells().get(0).getCellId()).isEqualTo(256L);
    assertThat(result.getAdjacentCells().get(0).getSignalQuality())
        .isEqualTo(SignalQualityDto.fromIndexValue(7));
  }

  @Test
  void testUnhappy() throws Exception {

    // SETUP

    final GetResult result = mock(GetResult.class);
    when(result.getResultCode()).thenReturn(AccessResultCode.HARDWARE_FAULT);

    when(this.dlmsHelper.getAndCheck(
            eq(this.connectionManager), eq(this.device), eq("Get GsmDiagnostic"), any()))
        .thenReturn(Collections.singletonList(result));

    // CALL
    try {
      this.executor.execute(
          this.connectionManager, this.device, this.request, this.messageMetadata);
      fail("When result contains failure, then execute should fail");
    } catch (final ProtocolAdapterException e) {
      assertThat(e.getMessage())
          .isEqualTo("Get gsm diagnostic failed for " + this.device.getDeviceId());
    }
  }

  private String createAttributeAddress(final int attributeId) {
    return String.format("{%s,%s,%d}", this.classId, this.obisCode, attributeId);
  }

  private DlmsDevice createDevice(final Protocol protocol, final CommunicationMethod method) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    device.setCommunicationMethod(method.name());
    device.setDeviceIdentification("1234567890");
    return device;
  }
}
