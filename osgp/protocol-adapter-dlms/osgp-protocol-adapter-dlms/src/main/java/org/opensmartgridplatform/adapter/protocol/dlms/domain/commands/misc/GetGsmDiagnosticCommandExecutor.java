//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil.describeGetResults;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute.ADJACENT_CELLS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute.CELL_INFO;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute.CIRCUIT_SWITCHED_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute.MODEM_REGISTRATION_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute.OPERATOR;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute.PACKET_SWITCHED_STATUS;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdjacentCellInfoDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BitErrorRateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CellInfoDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CircuitSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemRegistrationStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PacketSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SignalQualityDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetGsmDiagnosticCommandExecutor
    extends AbstractCommandExecutor<GetGsmDiagnosticRequestDto, GetGsmDiagnosticResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetGsmDiagnosticCommandExecutor.class);

  private static final int RESULT_OPERATOR_INDEX = 0;
  private static final int RESULT_MODEM_REGISTRATION_STATUS_INDEX = 1;
  private static final int RESULT_CIRCUIT_SWITCHED_STATUS_INDEX = 2;
  private static final int RESULT_PACKET_SWITCHED_STATUS_INDEX = 3;
  private static final int RESULT_CELL_INFO_INDEX = 4;
  private static final int RESULT_ADJACENT_CELLS_INDEX = 5;
  private static final int RESULT_CAPTURE_TIME_INDEX = 6;

  private static final int CELL_INFO_CELL_ID_INDEX = 0;
  private static final int CELL_INFO_LOCATION_ID_INDEX = 1;
  private static final int CELL_INFO_SIGNAL_QUALITY_INDEX = 2;
  private static final int CELL_INFO_BIT_ERROR_RATE_INDEX = 3;
  private static final int CELL_INFO_MOBILE_COUNTRY_CODE_INDEX = 4;
  private static final int CELL_INFO_MOBILE_NETWORK_CODE_INDEX = 5;
  private static final int CELL_INFO_CHANNEL_NUMBER_INDEX = 6;

  private static final int ADJACENT_CELLS_CELL_ID_INDEX = 0;
  private static final int ADJACENT_CELLS_SIGNAL_QUALITY_INDEX = 1;

  private final DlmsHelper dlmsHelper;

  private final ObjectConfigService objectConfigService;

  @Autowired
  public GetGsmDiagnosticCommandExecutor(
      final DlmsHelper dlmsHelper, final ObjectConfigService objectConfigService) {
    super(GetGsmDiagnosticRequestDto.class);

    this.dlmsHelper = dlmsHelper;
    this.objectConfigService = objectConfigService;
  }

  @Override
  public GetGsmDiagnosticRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return new GetGsmDiagnosticRequestDto();
  }

  @Override
  public GetGsmDiagnosticResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetGsmDiagnosticRequestDto getGsmDiagnosticQuery,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final CosemObject cosemObject = this.getCosemObject(device);

    final AttributeAddress[] attributeAddresses =
        this.getAttributeAddresses(cosemObject).toArray(new AttributeAddress[0]);

    final String description =
        "Get GsmDiagnostic, retrieve attributes: "
            + JdlmsObjectToStringUtil.describeAttributes(attributeAddresses);
    conn.getDlmsMessageListener().setDescription(description);
    LOGGER.debug(description);

    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(conn, device, "Get GsmDiagnostic", attributeAddresses);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("GetResultList: {}", describeGetResults(getResultList));
    }

    if (!getResultList.stream()
        .allMatch(result -> result.getResultCode() == AccessResultCode.SUCCESS)) {
      throw new ProtocolAdapterException("Get gsm diagnostic failed for " + device.getDeviceId());
    }

    return this.createGetGsmDiagnosticResponse(getResultList);
  }

  public CosemObject getCosemObject(final DlmsDevice device) throws ProtocolAdapterException {

    final DlmsObjectType dlmsObjectType = determineDlsmObjectType(device);

    try {

      // Get object (should be the first in the list)
      return this.objectConfigService.getCosemObject(
          device.getProtocolName(), device.getProtocolVersion(), dlmsObjectType);

    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException("Error in object config", e);
    }
  }

  private static DlmsObjectType determineDlsmObjectType(final DlmsDevice device)
      throws ProtocolAdapterException {
    final String diagnostic = String.format("%s_DIAGNOSTIC", device.getCommunicationMethod());
    final DlmsObjectType dlmsObjectType;
    try {
      dlmsObjectType = DlmsObjectType.valueOf(diagnostic);
    } catch (final IllegalArgumentException e) {
      throw new ProtocolAdapterException(
          String.format(
              "Cannot find %s for communication method %s",
              diagnostic, device.getCommunicationMethod()));
    }
    return dlmsObjectType;
  }

  private List<AttributeAddress> getAttributeAddresses(final CosemObject cosemObject) {
    final int classId = cosemObject.getClassId();
    final ObisCode obisCode = new ObisCode(cosemObject.getObis());

    return Arrays.asList( //
        new AttributeAddress(classId, obisCode, OPERATOR.attributeId()),
        new AttributeAddress(classId, obisCode, MODEM_REGISTRATION_STATUS.attributeId()),
        new AttributeAddress(classId, obisCode, CIRCUIT_SWITCHED_STATUS.attributeId()),
        new AttributeAddress(classId, obisCode, PACKET_SWITCHED_STATUS.attributeId()),
        new AttributeAddress(classId, obisCode, CELL_INFO.attributeId()),
        new AttributeAddress(classId, obisCode, ADJACENT_CELLS.attributeId()));
    // Reading of capture_time is disabled for now, because the jDLMS library appears to handle
    // the COSEM date-time in the response incorrectly. Also see comment in getCaptureTime.
    // new AttributeAddress(classId, obisCode, CAPTURE_TIME.attributeId())
  }

  private GetGsmDiagnosticResponseDto createGetGsmDiagnosticResponse(
      final List<GetResult> getResultList) throws ProtocolAdapterException {

    final String operator = this.getOperator(getResultList);
    final ModemRegistrationStatusDto registrationStatus = this.getRegistrationStatus(getResultList);
    final CircuitSwitchedStatusDto circuitSwitchedStatus =
        this.getCircuitSwitchedStatus(getResultList);
    final PacketSwitchedStatusDto packetSwitchedStatusDto =
        this.getPacketSwitchedStatus(getResultList);
    final CellInfoDto cellInfo = this.getCellInfo(getResultList);
    final List<AdjacentCellInfoDto> adjacentCells = this.getAdjacentCells(getResultList);
    final Date captureTimeDto = this.getCaptureTime(getResultList);

    return new GetGsmDiagnosticResponseDto(
        operator,
        registrationStatus,
        circuitSwitchedStatus,
        packetSwitchedStatusDto,
        cellInfo,
        adjacentCells,
        captureTimeDto);
  }

  private String getOperator(final List<GetResult> getResultList) {
    final GetResult result = getResultList.get(RESULT_OPERATOR_INDEX);
    if (this.isResultSuccess(result)) {
      final byte[] bytes = result.getResultData().getValue();
      return new String(bytes, StandardCharsets.US_ASCII);
    } else {
      return null;
    }
  }

  private ModemRegistrationStatusDto getRegistrationStatus(final List<GetResult> getResultList) {
    final GetResult result = getResultList.get(RESULT_MODEM_REGISTRATION_STATUS_INDEX);
    if (this.isResultSuccess(result)) {
      return ModemRegistrationStatusDto.fromIndexValue(result.getResultData().getValue());
    } else {
      return null;
    }
  }

  private CircuitSwitchedStatusDto getCircuitSwitchedStatus(final List<GetResult> getResultList) {
    final GetResult result = getResultList.get(RESULT_CIRCUIT_SWITCHED_STATUS_INDEX);
    if (this.isResultSuccess(result)) {
      return CircuitSwitchedStatusDto.fromIndexValue(result.getResultData().getValue());
    } else {
      return null;
    }
  }

  private PacketSwitchedStatusDto getPacketSwitchedStatus(final List<GetResult> getResultList) {
    final GetResult result = getResultList.get(RESULT_PACKET_SWITCHED_STATUS_INDEX);
    if (this.isResultSuccess(result)) {
      return PacketSwitchedStatusDto.fromIndexValue(result.getResultData().getValue());
    } else {
      return null;
    }
  }

  private CellInfoDto getCellInfo(final List<GetResult> getResultList) {
    final GetResult result = getResultList.get(RESULT_CELL_INFO_INDEX);
    if (result.getResultCode() != AccessResultCode.SUCCESS) {
      return null;
    }

    final List<DataObject> cellInfoDataObjects = result.getResultData().getValue();

    if (cellInfoDataObjects != null) {
      return new CellInfoDto(
          cellInfoDataObjects.get(CELL_INFO_CELL_ID_INDEX).getValue(),
          cellInfoDataObjects.get(CELL_INFO_LOCATION_ID_INDEX).getValue(),
          SignalQualityDto.fromIndexValue(
              (short) cellInfoDataObjects.get(CELL_INFO_SIGNAL_QUALITY_INDEX).getValue()),
          BitErrorRateDto.fromIndexValue(
              (short) cellInfoDataObjects.get(CELL_INFO_BIT_ERROR_RATE_INDEX).getValue()),
          cellInfoDataObjects.get(CELL_INFO_MOBILE_COUNTRY_CODE_INDEX).getValue(),
          cellInfoDataObjects.get(CELL_INFO_MOBILE_NETWORK_CODE_INDEX).getValue(),
          cellInfoDataObjects.get(CELL_INFO_CHANNEL_NUMBER_INDEX).getValue());
    } else {
      return null;
    }
  }

  private List<AdjacentCellInfoDto> getAdjacentCells(final List<GetResult> getResultList) {
    final GetResult result = getResultList.get(RESULT_ADJACENT_CELLS_INDEX);
    if (result.getResultCode() != AccessResultCode.SUCCESS) {
      return Collections.emptyList();
    }

    final List<DataObject> adjacentCellsDataObjects = result.getResultData().getValue();

    if (adjacentCellsDataObjects == null) {
      return Collections.emptyList();
    } else {
      return adjacentCellsDataObjects.stream()
          .map(
              cellInfo -> {
                final List<DataObject> adjacentCell = cellInfo.getValue();
                return new AdjacentCellInfoDto(
                    adjacentCell.get(ADJACENT_CELLS_CELL_ID_INDEX).getValue(),
                    SignalQualityDto.fromIndexValue(
                        (short) adjacentCell.get(ADJACENT_CELLS_SIGNAL_QUALITY_INDEX).getValue()));
              })
          .toList();
    }
  }

  private Date getCaptureTime(final List<GetResult> getResultList) throws ProtocolAdapterException {
    // Reading of capture_time is disabled, so return null here. Also see comment in
    // createAttributeAddresses.
    if (RESULT_CAPTURE_TIME_INDEX >= getResultList.size()) {
      return null;
    }

    final GetResult result = getResultList.get(RESULT_CAPTURE_TIME_INDEX);
    if (this.isResultSuccess(result)) {
      final CosemDateTimeDto cosemDateTime =
          this.dlmsHelper.readDateTime(result.getResultData(), "Clock from gsm diagnostic");

      final Date captureTime;
      if (cosemDateTime.isDateTimeSpecified()) {
        captureTime = cosemDateTime.asDateTime().toDate();
      } else {
        throw new ProtocolAdapterException("Unexpected values in gsm diagnostic capture time");
      }

      return captureTime;
    } else {
      return null;
    }
  }

  private boolean isResultSuccess(final GetResult result) {
    return result.getResultCode() == AccessResultCode.SUCCESS
        && result.getResultData() != null
        && result.getResultData().getValue() != null;
  }
}
