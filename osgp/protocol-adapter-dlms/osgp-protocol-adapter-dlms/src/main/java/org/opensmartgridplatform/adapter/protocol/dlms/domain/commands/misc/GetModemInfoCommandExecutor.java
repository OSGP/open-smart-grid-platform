/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
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
import java.util.List;

import org.joda.time.DateTime;
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemInfoDto;
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
    public GetModemInfoCommandExecutor(final DlmsHelper dlmsHelper,
            final DlmsObjectConfigService dlmsObjectConfigService) {
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
            final GetModemInfoRequestDto getModemInfoQuery
    ) throws ProtocolAdapterException {

        final DlmsObject dlmsObject = this.dlmsObjectConfigService
                .findDlmsObjectForCommunicationMethod(device, DlmsObjectType.MODEM_INFO);

        final AttributeAddress[] addresses = this.createAttributeAddresses(dlmsObject);

        conn.getDlmsMessageListener().setDescription(
                "Get ModemInfo, retrieve attributes: " + JdlmsObjectToStringUtil.describeAttributes(addresses));

        final List<GetResult> getResultList = this.dlmsHelper.getAndCheck(conn, device, "Get ModemInfo", addresses);

        if (getResultList.stream().noneMatch(result -> result.getResultCode() == AccessResultCode.SUCCESS )) {
            throw new ProtocolAdapterException("Get modem info failed for " + device.getDeviceId());
        }

        return this.createGetModemInfoResponse(getResultList);
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

    private GetModemInfoResponseDto createGetModemInfoResponse(
            final List<GetResult> getResultList
    ) throws ProtocolAdapterException {

        final String operator = this.getOperator(getResultList);
        final ModemRegistrationStatusDto registrationStatus = this.getRegistrationStatus(getResultList);
        final CircuitSwitchedStatusDto circuitSwitchedStatus = this.getCircuitSwitchedStatus(getResultList);
        final PacketSwitchedStatusDto packetSwitchedStatusDto = this.getPacketSwitchedStatus(getResultList);
        final CellInfo cellInfo = this.getCellInfo(getResultList);
        final AdjacentCellsInfo adjacentCellsInfo = this.getAdjacentCellsInfo(getResultList);
        final DateTime captureTimeDto = this.getCaptureTime(getResultList);

        final ModemInfoDto modemInfoDto = new ModemInfoDto(
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
                captureTimeDto
        );

        return new GetModemInfoResponseDto(modemInfoDto);
    }

    private String getOperator(final List<GetResult> getResultList) {
        final GetResult operatorResult = getResultList.get(RESULT_OPERATOR_INDEX);
        if (operatorResult.getResultCode() == AccessResultCode.SUCCESS) {
            final byte[] bytes = operatorResult.getResultData().getValue();
            return new String(bytes, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }

    private ModemRegistrationStatusDto getRegistrationStatus(final List<GetResult> getResultList) {
        final GetResult registrationStatusResult = getResultList.get(RESULT_MODEM_REGISTRATION_STATUS_INDEX);
        if (registrationStatusResult.getResultCode() == AccessResultCode.SUCCESS) {
            return ModemRegistrationStatusDto.fromValue(registrationStatusResult.getResultData().getValue());
        } else {
            return null;
        }
    }

    private CircuitSwitchedStatusDto getCircuitSwitchedStatus(final List<GetResult> getResultList) {
        final GetResult circuitSwitchedStatusResult = getResultList.get(RESULT_CIRCUIT_SWITCHED_STATUS_INDEX);
        if (circuitSwitchedStatusResult.getResultCode() == AccessResultCode.SUCCESS) {
            return CircuitSwitchedStatusDto.fromValue(circuitSwitchedStatusResult.getResultData().getValue());
        } else {
            return null;
        }
    }

    private PacketSwitchedStatusDto getPacketSwitchedStatus(final List<GetResult> getResultList) {
        final GetResult packetSwitchedStatusResult = getResultList.get(RESULT_PACKET_SWITCHED_STATUS_INDEX);
        if (packetSwitchedStatusResult.getResultCode() == AccessResultCode.SUCCESS) {
            return PacketSwitchedStatusDto.fromValue(packetSwitchedStatusResult.getResultData().getValue());
        } else {
            return null;
        }
    }

    private CellInfo getCellInfo(final List<GetResult> getResultList) {
        final GetResult cellInfoResult = getResultList.get(RESULT_CELL_INFO_INDEX);
        if (cellInfoResult.getResultCode() != AccessResultCode.SUCCESS) {
            return null;
        }

        final List<DataObject> cellInfoDataObjects = cellInfoResult.getResultData().getValue();

        final CellInfo cellInfo = new CellInfo();

        if (cellInfoDataObjects != null) {
            cellInfo.cellId = this.longToByteArray(cellInfoDataObjects.get(CELL_INFO_CELL_ID_INDEX).getValue());
            cellInfo.locationId = this.intToByteArray(cellInfoDataObjects.get(CELL_INFO_LOCATION_ID_INDEX).getValue());
            cellInfo.signalQualityDto = SignalQualityDto
                    .fromValue((short) cellInfoDataObjects.get(CELL_INFO_SIGNAL_QUALITY_INDEX).getValue());
            cellInfo.bitErrorRateDto = BitErrorRateDto
                    .fromValue((short) cellInfoDataObjects.get(CELL_INFO_BIT_ERROR_RATE_INDEX).getValue());
            cellInfo.mobileCountryCode = cellInfoDataObjects.get(CELL_INFO_MOBILE_COUNTRY_CODE_INDEX).getValue();
            cellInfo.mobileNetworkCode = cellInfoDataObjects.get(CELL_INFO_MOBILE_NETWORK_CODE_INDEX).getValue();
            cellInfo.channelNumber = cellInfoDataObjects.get(CELL_INFO_CHANNEL_NUMBER_INDEX).getValue();
        }

        return cellInfo;
    }

    private AdjacentCellsInfo getAdjacentCellsInfo(final List<GetResult> getResultList) {
        final GetResult adjacentCellsResult = getResultList.get(RESULT_ADJACENT_CELLS_INDEX);
        if (adjacentCellsResult.getResultCode() != AccessResultCode.SUCCESS) {
            return null;
        }

        final List<DataObject> adjacentCellsDataObjects = adjacentCellsResult.getResultData().getValue();

        final AdjacentCellsInfo adjacentCellsInfo = new AdjacentCellsInfo();

        if (adjacentCellsDataObjects != null) {
            adjacentCellsInfo.numberOfAdjacentCells = adjacentCellsDataObjects.size();
            if (adjacentCellsDataObjects.size() > 0) {
                final List<DataObject> firstAdjacentCell = adjacentCellsDataObjects.get(0).getValue();
                adjacentCellsInfo.adjacentCellId = this
                        .longToByteArray(firstAdjacentCell.get(ADJACENT_CELLS_CELL_ID_INDEX).getValue());
                adjacentCellsInfo.signalQualityDto = SignalQualityDto
                        .fromValue((short) firstAdjacentCell.get(ADJACENT_CELLS_SIGNAL_QUALITY_INDEX).getValue());
            }
        }

        return adjacentCellsInfo;
    }

    private List<DataObject> getAdjacentCells(final List<GetResult> getResultList) {
        final GetResult adjacentCellsResult = getResultList.get(RESULT_ADJACENT_CELLS_INDEX);
        if (adjacentCellsResult.getResultCode() == AccessResultCode.SUCCESS) {
            return adjacentCellsResult.getResultData().getValue();
        } else {
            return null;
        }
    }

    private DateTime getCaptureTime(final List<GetResult> getResultList) throws ProtocolAdapterException {
        final GetResult captureTimeResult = getResultList.get(RESULT_CAPTURE_TIME_INDEX);
        if (captureTimeResult.getResultCode() == AccessResultCode.SUCCESS) {
            final CosemDateTimeDto cosemDateTime = this.dlmsHelper
                    .readDateTime(captureTimeResult.getResultData(), "Clock from modem info");

            final DateTime captureTime;
            if (cosemDateTime.isDateTimeSpecified()) {
                captureTime = cosemDateTime.asDateTime();
            } else {
                throw new ProtocolAdapterException("Unexpected values in modem info capture time");
            }

            return captureTime;
        } else {
            return null;
        }
    }

    private byte[] longToByteArray(final long value) {
        return new byte[] {
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24) };
    }

    private byte[] intToByteArray(final int value) {
        return new byte[] {
                (byte) value,
                (byte) (value >> 8) };
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
