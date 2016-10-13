/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DeviceConnector;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.FirmwareModuleType;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FirmwareVersionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDto;

@Component
public class GetFirmwareVersionsCommandExecutor extends AbstractCommandExecutor<Void, List<FirmwareVersionDto>> {

    private static final int CLASS_ID = 1;
    private static final int ATTRIBUTE_ID = 2;

    private static final ObisCode OBIS_CODE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.0.0.2.0.255");
    private static final ObisCode OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.1.0.2.0.255");
    private static final ObisCode OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.2.0.2.0.255");

    private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = {
        new AttributeAddress(CLASS_ID, OBIS_CODE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
        new AttributeAddress(CLASS_ID, OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
        new AttributeAddress(CLASS_ID, OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID) };

    private static final int INDEX_ACTIVE_FIRMWARE_VERSION = 0;
    private static final int INDEX_MODULE_ACTIVE_FIRMWARE_VERSION = 1;
    private static final int INDEX_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION = 2;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    public GetFirmwareVersionsCommandExecutor() {
        super(GetFirmwareVersionRequestDto.class);
    }

    @Override
    public Void fromBundleRequestInput(final ActionRequestDto bundleInput) throws ProtocolAdapterException {

        this.checkActionRequestType(bundleInput);

        return null;
    }

    @Override
    public ActionResponseDto asBundleResponse(final List<FirmwareVersionDto> executionResult)
            throws ProtocolAdapterException {

        return new FirmwareVersionResponseDto(executionResult);
    }

    @Override
    public List<FirmwareVersionDto> execute(final DeviceConnector conn, final DlmsDevice device, final Void useless)
            throws ProtocolAdapterException {

        final List<FirmwareVersionDto> resultList = new ArrayList<>();

        final List<GetResult> getResultList = this.dlmsHelperService.getAndCheck(conn.connection(), device,
                "retrieve firmware versions", ATTRIBUTE_ADDRESSES);

        resultList.add(new FirmwareVersionDto(FirmwareModuleType.ACTIVE_FIRMWARE, this.dlmsHelperService.readString(
                getResultList.get(INDEX_ACTIVE_FIRMWARE_VERSION).getResultData(),
                FirmwareModuleType.ACTIVE_FIRMWARE.getDescription())));
        resultList.add(new FirmwareVersionDto(FirmwareModuleType.MODULE_ACTIVE, this.dlmsHelperService.readString(
                getResultList.get(INDEX_MODULE_ACTIVE_FIRMWARE_VERSION).getResultData(),
                FirmwareModuleType.MODULE_ACTIVE.getDescription())));
        resultList.add(new FirmwareVersionDto(FirmwareModuleType.COMMUNICATION, this.dlmsHelperService.readString(
                getResultList.get(INDEX_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION).getResultData(),
                FirmwareModuleType.COMMUNICATION.getDescription())));

        return resultList;
    }
}
