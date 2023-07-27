// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.CommunicationMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdjacentCellInfoDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BitErrorRateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CellInfoDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CircuitSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemRegistrationStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PacketSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SignalQualityDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetGsmDiagnosticCommandExecutorIntegrationTest {

  private GetGsmDiagnosticCommandExecutor executor;

  private DlmsConnectionManagerStub connectionManagerStub;
  private DlmsConnectionStub connectionStub;

  private ObjectConfigService objectConfigService;

  @ParameterizedTest
  @CsvSource({
    "DSMR_4_2_2,CDMA,false",
    "DSMR_4_2_2,GPRS,false",
    "DSMR_4_2_2,LTE,false",
    "SMR_4_3,CDMA,true",
    "SMR_4_3,GPRS,false",
    "SMR_4_3,LTE,false",
    "SMR_5_0_0,CDMA,true",
    "SMR_5_0_0,GPRS,true",
    "SMR_5_0_0,LTE,false",
    "SMR_5_1,CDMA,true",
    "SMR_5_1,GPRS,true",
    "SMR_5_1,LTE,false",
    "SMR_5_2,CDMA,true",
    "SMR_5_2,GPRS,true",
    "SMR_5_2,LTE,true",
    "SMR_5_5,CDMA,true",
    "SMR_5_5,GPRS,true",
    "SMR_5_5,LTE,true"
  })
  void executeAndValidate(
      final String protocol, final String communicationMethod, final boolean succeeds)
      throws Exception {

    this.executeAndValidate(
        Protocol.valueOf(protocol), CommunicationMethod.valueOf(communicationMethod), succeeds);
  }

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    this.objectConfigService = new ObjectConfigService(null);

    final DlmsHelper dlmsHelper = new DlmsHelper();

    this.executor = new GetGsmDiagnosticCommandExecutor(dlmsHelper, this.objectConfigService);

    this.connectionStub = new DlmsConnectionStub();
    this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);
    this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));
  }

  private void executeAndValidate(
      final Protocol protocol, final CommunicationMethod method, final boolean succeeds)
      throws Exception {

    // SETUP
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    // Reset stub
    this.connectionStub.clearRequestedAttributeAddresses();

    // Create device with requested protocol version and communication method
    final DlmsDevice device = this.createDlmsDevice(protocol, method);

    // Create request object
    final GetGsmDiagnosticRequestDto request = new GetGsmDiagnosticRequestDto();

    // Get expected addresses
    final AttributeAddress expectedAddressOperator = this.createAttributeAddress(method, 2);
    final AttributeAddress expectedAddressRegistrationStatus =
        this.createAttributeAddress(method, 3);
    final AttributeAddress expectedAddressCsStatus = this.createAttributeAddress(method, 4);
    final AttributeAddress expectedAddressPsStatus = this.createAttributeAddress(method, 5);
    final AttributeAddress expectedAddressCellInfo = this.createAttributeAddress(method, 6);
    final AttributeAddress expectedAddressAdjacentCells = this.createAttributeAddress(method, 7);
    final AttributeAddress expectedAddressCaptureTime = this.createAttributeAddress(method, 8);
    // Reading of capture_time is disabled for now, therefore only 6 addresses expected
    final int expectedTotalNumberOfAttributeAddresses = 6;

    // Set responses in stub
    this.setResponseForOperator(expectedAddressOperator);
    this.setResponseForRegistrationStatus(expectedAddressRegistrationStatus);
    this.setResponseForCsStatus(expectedAddressCsStatus);
    this.setResponseForPsStatus(expectedAddressPsStatus);
    this.setResponseForCellInfo(expectedAddressCellInfo);
    this.setResponseForAdjacentCells(expectedAddressAdjacentCells);
    this.setResponseForCaptureTime(expectedAddressCaptureTime);

    // CALL
    final GetGsmDiagnosticResponseDto response;

    if (!succeeds) {
      final Exception exception =
          Assertions.assertThrows(
              IllegalArgumentException.class,
              () ->
                  this.executor.execute(
                      this.connectionManagerStub, device, request, messageMetadata));

      assertThat(exception.getMessage())
          .isEqualTo(
              String.format(
                  "No object found of type %s_DIAGNOSTIC in profile %s version %s",
                  method.name(), protocol.getName(), protocol.getVersion()));
      return;
    } else {
      response =
          this.executor.execute(this.connectionManagerStub, device, request, messageMetadata);
    }

    // VERIFY
    // Get resulting requests from connection stub
    final List<AttributeAddress> requestedAttributeAddresses =
        this.connectionStub.getRequestedAttributeAddresses();
    assertThat(requestedAttributeAddresses).hasSize(expectedTotalNumberOfAttributeAddresses);

    // Check response
    assertThat(response).isNotNull();
    assertThat(response).isNotNull();
    assertThat(response.getOperator()).isEqualTo("Operator");
    assertThat(response.getModemRegistrationStatus())
        .isEqualTo(ModemRegistrationStatusDto.REGISTERED_ROAMING);
    assertThat(response.getCircuitSwitchedStatus()).isEqualTo(CircuitSwitchedStatusDto.INACTIVE);
    assertThat(response.getPacketSwitchedStatus()).isEqualTo(PacketSwitchedStatusDto.CDMA);
    final CellInfoDto cellInfo = response.getCellInfo();
    assertThat(cellInfo.getCellId()).isEqualTo(93L);
    assertThat(cellInfo.getLocationId()).isEqualTo(2232);
    assertThat(cellInfo.getSignalQuality()).isEqualTo(SignalQualityDto.MINUS_87_DBM);
    assertThat(cellInfo.getBitErrorRate()).isEqualTo(BitErrorRateDto.RXQUAL_6);
    assertThat(cellInfo.getMobileCountryCode()).isEqualTo(204);
    assertThat(cellInfo.getMobileNetworkCode()).isEqualTo(66);
    assertThat(cellInfo.getChannelNumber()).isEqualTo(107);
    final List<AdjacentCellInfoDto> adjacentCells = response.getAdjacentCells();
    assertThat(adjacentCells).hasSize(3);
    assertThat(adjacentCells.get(0).getCellId()).isEqualTo(85L);
    assertThat(adjacentCells.get(0).getSignalQuality()).isEqualTo(SignalQualityDto.MINUS_65_DBM);
    // Reading of capture_time is disabled, so don't check the capture time
    // assertThat(response.getCaptureTime())
    //    .isEqualTo(new DateTime(2021, 4, 1, 9, 28, DateTimeZone.UTC).toDate());
  }

  private DlmsDevice createDlmsDevice(final Protocol protocol, final CommunicationMethod method) {
    final DlmsDevice device = new DlmsDevice();
    device.setDeviceIdentification("123456789012");
    device.setProtocol(protocol);
    device.setCommunicationMethod(method.getMethodName());
    return device;
  }

  private AttributeAddress createAttributeAddress(
      final CommunicationMethod method, final int attributeId) throws Exception {

    if (method == CommunicationMethod.GPRS) {
      return new AttributeAddress(47, new ObisCode(0, 0, 25, 6, 0, 255), attributeId);
    } else if (method == CommunicationMethod.CDMA) {
      return new AttributeAddress(47, new ObisCode(0, 1, 25, 6, 0, 255), attributeId);
    } else if (method == CommunicationMethod.LTE) {
      return new AttributeAddress(47, new ObisCode(0, 2, 25, 6, 0, 255), attributeId);
    }

    throw new Exception("Invalid communication method " + method.name());
  }

  private void setResponseForOperator(final AttributeAddress address) {
    final DataObject responseDataObject =
        DataObject.newVisibleStringData("Operator".getBytes(StandardCharsets.US_ASCII));
    this.connectionStub.addReturnValue(address, responseDataObject);
  }

  private void setResponseForRegistrationStatus(final AttributeAddress address) {
    final DataObject responseDataObject = DataObject.newEnumerateData(5);
    this.connectionStub.addReturnValue(address, responseDataObject);
  }

  private void setResponseForCsStatus(final AttributeAddress address) {
    final DataObject responseDataObject = DataObject.newEnumerateData(0);
    this.connectionStub.addReturnValue(address, responseDataObject);
  }

  private void setResponseForPsStatus(final AttributeAddress address) {
    final DataObject responseDataObject = DataObject.newEnumerateData(6);
    this.connectionStub.addReturnValue(address, responseDataObject);
  }

  private void setResponseForCellInfo(final AttributeAddress address) {
    final DataObject cellId = DataObject.newUInteger32Data(93);
    final DataObject locationId = DataObject.newUInteger16Data(2232);
    final DataObject signalQuality = DataObject.newUInteger8Data((short) 13);
    final DataObject ber = DataObject.newUInteger8Data((short) 6);
    final DataObject mcc = DataObject.newUInteger16Data(204);
    final DataObject mnc = DataObject.newUInteger16Data(66);
    final DataObject channelNumber = DataObject.newUInteger32Data(107);

    final DataObject responseDataObject =
        DataObject.newStructureData(
            cellId, locationId, signalQuality, ber, mcc, mnc, channelNumber);
    this.connectionStub.addReturnValue(address, responseDataObject);
  }

  private void setResponseForAdjacentCells(final AttributeAddress address) {
    final DataObject cellId1 = DataObject.newUInteger32Data(85);
    final DataObject signalQuality1 = DataObject.newUInteger8Data((short) 24);
    final DataObject adjacentCells1 = DataObject.newStructureData(cellId1, signalQuality1);
    final DataObject cellId2 = DataObject.newUInteger32Data(0);
    final DataObject signalQuality2 = DataObject.newUInteger8Data((short) 0);
    final DataObject adjacentCells2 = DataObject.newStructureData(cellId2, signalQuality2);
    final DataObject cellId3 = DataObject.newUInteger32Data(303);
    final DataObject signalQuality3 = DataObject.newUInteger8Data((short) 31);
    final DataObject adjacentCells3 = DataObject.newStructureData(cellId3, signalQuality3);

    final DataObject responseDataObject =
        DataObject.newArrayData(Arrays.asList(adjacentCells1, adjacentCells2, adjacentCells3));
    this.connectionStub.addReturnValue(address, responseDataObject);
  }

  private void setResponseForCaptureTime(final AttributeAddress address) {
    final DataObject responseDataObject =
        DataObject.newDateTimeData(new CosemDateTime(2021, 4, 1, 9, 28, 0, 0));
    this.connectionStub.addReturnValue(address, responseDataObject);
  }
}
