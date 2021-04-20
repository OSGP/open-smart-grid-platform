/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsModemInfo.ATTRIBUTE_ID_ADJACENT_CELLS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsModemInfo.ATTRIBUTE_ID_CAPTURE_TIME;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsModemInfo.ATTRIBUTE_ID_CELL_INFO;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsModemInfo.ATTRIBUTE_ID_CIRCUIT_SWITCHED_STATUS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsModemInfo.ATTRIBUTE_ID_MODEM_REGISTRATION_STATUS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsModemInfo.ATTRIBUTE_ID_OPERATOR;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsModemInfo.ATTRIBUTE_ID_PACKET_SWITCHED_STATUS;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BitErrorRateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CircuitSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetModemInfoRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetModemInfoResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemRegistrationStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PacketSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SignalQualityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GetModemInfoCommandExecutor
    extends AbstractCommandExecutor<GetModemInfoRequestDto, GetModemInfoResponseDto> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetModemInfoCommandExecutor.class);

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
  private final DlmsObjectConfigService dlmsObjectConfigService;

  @Autowired
  public GetModemInfoCommandExecutor(
      final DlmsHelper dlmsHelper, final DlmsObjectConfigService dlmsObjectConfigService) {
    super(GetModemInfoRequestDto.class);

    this.dlmsHelper = dlmsHelper;
    this.dlmsObjectConfigService = dlmsObjectConfigService;
  }

  @Override
  public GetModemInfoRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return new GetModemInfoRequestDto();
  }

  @Override
  public GetModemInfoResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetModemInfoRequestDto getModemInfoQuery)
      throws ProtocolAdapterException {

    final DlmsObject dlmsObject =
        this.dlmsObjectConfigService.findDlmsObjectForCommunicationMethod(
            device, DlmsObjectType.MODEM_INFO);

    final AttributeAddress[] addresses = this.createAttributeAddresses(dlmsObject);

    conn.getDlmsMessageListener()
        .setDescription(
            "Get ModemInfo, retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(addresses));

    LOGGER.info(
        "Get ModemInfo, retrieve attributes: "
            + JdlmsObjectToStringUtil.describeAttributes(addresses));

    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(conn, device, "Get ModemInfo", addresses);

    final String resultString =
        getResultList.stream().map(this::resultToString).collect(Collectors.joining("-", "{", "}"));
    LOGGER.info("GetResultList: {}", resultString);

    if (getResultList.stream()
        .noneMatch(result -> result.getResultCode() == AccessResultCode.SUCCESS)) {
      throw new ProtocolAdapterException("Get modem info failed for " + device.getDeviceId());
    }

    return this.createGetModemInfoResponse(getResultList);
  }

  private String resultToString(final GetResult result) {
    if (result != null) {
      String code = "";
      String data = "";
      if (result.getResultCode() != null) {
        code = result.getResultCode().toString();
      } else {
        code = " Result code is null ";
      }
      if (result.getResultData() != null) {
        data = result.getResultData().toString();
      } else {
        data = " Result data is null ";
      }
      return code + ", " + data;
    } else {
      return "Result is null ";
    }
  }

  private AttributeAddress[] createAttributeAddresses(final DlmsObject dlmsObject) {
    final int classId = dlmsObject.getClassId();
    final ObisCode obisCode = dlmsObject.getObisCode();

    return new AttributeAddress[] {
      new AttributeAddress(classId, obisCode, ATTRIBUTE_ID_OPERATOR),
      new AttributeAddress(classId, obisCode, ATTRIBUTE_ID_MODEM_REGISTRATION_STATUS),
      new AttributeAddress(classId, obisCode, ATTRIBUTE_ID_CIRCUIT_SWITCHED_STATUS),
      new AttributeAddress(classId, obisCode, ATTRIBUTE_ID_PACKET_SWITCHED_STATUS),
      new AttributeAddress(classId, obisCode, ATTRIBUTE_ID_CELL_INFO),
      new AttributeAddress(classId, obisCode, ATTRIBUTE_ID_ADJACENT_CELLS),
      new AttributeAddress(classId, obisCode, ATTRIBUTE_ID_CAPTURE_TIME)
    };
  }

  private GetModemInfoResponseDto createGetModemInfoResponse(final List<GetResult> getResultList)
      throws ProtocolAdapterException {

    final String operator = this.getOperator(getResultList);
    final ModemRegistrationStatusDto registrationStatus = this.getRegistrationStatus(getResultList);
    final CircuitSwitchedStatusDto circuitSwitchedStatus =
        this.getCircuitSwitchedStatus(getResultList);
    final PacketSwitchedStatusDto packetSwitchedStatusDto =
        this.getPacketSwitchedStatus(getResultList);
    final CellInfo cellInfo = this.getCellInfo(getResultList);
    final AdjacentCellsInfo adjacentCellsInfo = this.getAdjacentCellsInfo(getResultList);
    final Date captureTimeDto = this.getCaptureTime(getResultList);

    return new GetModemInfoResponseDto(
        operator,
        registrationStatus,
        circuitSwitchedStatus,
        packetSwitchedStatusDto,
        cellInfo.cellId,
        cellInfo.locationId,
        cellInfo.signalQualityDto,
        cellInfo.bitErrorRateDto,
        cellInfo.mobileCountryCode,
        cellInfo.mobileNetworkCode,
        cellInfo.channelNumber,
        adjacentCellsInfo.numberOfAdjacentCells,
        adjacentCellsInfo.adjacentCellId,
        adjacentCellsInfo.signalQualityDto,
        captureTimeDto);
  }

  private String getOperator(final List<GetResult> getResultList) {
    final GetResult result = getResultList.get(RESULT_OPERATOR_INDEX);
    if (this.isResultSuccess(result)) {
      final byte[] bytes = result.getResultData().getValue();
      return new String(bytes, StandardCharsets.UTF_8);
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

  private CellInfo getCellInfo(final List<GetResult> getResultList) {
    final GetResult result = getResultList.get(RESULT_CELL_INFO_INDEX);
    if (result.getResultCode() != AccessResultCode.SUCCESS) {
      return null;
    }

    final List<DataObject> cellInfoDataObjects = result.getResultData().getValue();

    final CellInfo cellInfo = new CellInfo();

    if (cellInfoDataObjects != null) {
      cellInfo.cellId =
          this.longToByteArray(cellInfoDataObjects.get(CELL_INFO_CELL_ID_INDEX).getValue());
      cellInfo.locationId =
          this.intToByteArray(cellInfoDataObjects.get(CELL_INFO_LOCATION_ID_INDEX).getValue());
      cellInfo.signalQualityDto =
          SignalQualityDto.fromIndexValue(
              (short) cellInfoDataObjects.get(CELL_INFO_SIGNAL_QUALITY_INDEX).getValue());
      cellInfo.bitErrorRateDto =
          BitErrorRateDto.fromIndexValue(
              (short) cellInfoDataObjects.get(CELL_INFO_BIT_ERROR_RATE_INDEX).getValue());
      cellInfo.mobileCountryCode =
          cellInfoDataObjects.get(CELL_INFO_MOBILE_COUNTRY_CODE_INDEX).getValue();
      cellInfo.mobileNetworkCode =
          cellInfoDataObjects.get(CELL_INFO_MOBILE_NETWORK_CODE_INDEX).getValue();
      cellInfo.channelNumber = cellInfoDataObjects.get(CELL_INFO_CHANNEL_NUMBER_INDEX).getValue();
    }

    return cellInfo;
  }

  private AdjacentCellsInfo getAdjacentCellsInfo(final List<GetResult> getResultList) {
    final GetResult result = getResultList.get(RESULT_ADJACENT_CELLS_INDEX);
    if (result.getResultCode() != AccessResultCode.SUCCESS) {
      return null;
    }

    final List<DataObject> adjacentCellsDataObjects = result.getResultData().getValue();

    final AdjacentCellsInfo adjacentCellsInfo = new AdjacentCellsInfo();

    if (adjacentCellsDataObjects != null) {
      adjacentCellsInfo.numberOfAdjacentCells = adjacentCellsDataObjects.size();
      if (!adjacentCellsDataObjects.isEmpty()) {
        final List<DataObject> firstAdjacentCell = adjacentCellsDataObjects.get(0).getValue();
        adjacentCellsInfo.adjacentCellId =
            this.longToByteArray(firstAdjacentCell.get(ADJACENT_CELLS_CELL_ID_INDEX).getValue());
        adjacentCellsInfo.signalQualityDto =
            SignalQualityDto.fromIndexValue(
                (short) firstAdjacentCell.get(ADJACENT_CELLS_SIGNAL_QUALITY_INDEX).getValue());
      }
    }

    return adjacentCellsInfo;
  }

  private Date getCaptureTime(final List<GetResult> getResultList) throws ProtocolAdapterException {
    final GetResult result = getResultList.get(RESULT_CAPTURE_TIME_INDEX);
    if (this.isResultSuccess(result)) {
      final CosemDateTimeDto cosemDateTime =
          this.dlmsHelper.readDateTime(result.getResultData(), "Clock from modem info");

      final Date captureTime;
      if (cosemDateTime.isDateTimeSpecified()) {
        captureTime = cosemDateTime.asDateTime().toDate();
      } else {
        throw new ProtocolAdapterException("Unexpected values in modem info capture time");
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

  private byte[] longToByteArray(final long value) {
    return new byte[] {
      (byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24)
    };
  }

  private byte[] intToByteArray(final int value) {
    return new byte[] {(byte) value, (byte) (value >> 8)};
  }

  private static class CellInfo {
    private byte[] cellId;
    private byte[] locationId;
    private SignalQualityDto signalQualityDto;
    private BitErrorRateDto bitErrorRateDto;
    private Integer mobileCountryCode;
    private Integer mobileNetworkCode;
    private Long channelNumber;
  }

  private static class AdjacentCellsInfo {
    private int numberOfAdjacentCells;
    private SignalQualityDto signalQualityDto;
    private byte[] adjacentCellId;
  }
}
