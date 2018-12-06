/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FirmwareVersionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDto;

@Component
public class GetFirmwareVersionsCommandExecutor extends AbstractCommandExecutor<Void, List<FirmwareVersionDto>> {

    private static final int CLASS_ID = 1;
    private static final int ATTRIBUTE_ID = 2;

    private static final ObisCode OBIS_CODE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.0.0.2.0.255");
    private static final ObisCode OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.1.0.2.0.255");
    private static final ObisCode OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.2.0.2.0.255");
    private static final ObisCode OBIS_CODE_MBUS_DRIVER_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.4.0.2.0.255");

    private static final List<FirmwareModuleType> FIRMWARE_MODULE_TYPES = asList(FirmwareModuleType.ACTIVE_FIRMWARE,
            FirmwareModuleType.MODULE_ACTIVE, FirmwareModuleType.COMMUNICATION, FirmwareModuleType.M_BUS_DRIVER_ACTIVE);

    private static final List<AttributeAddress> ALL_ATTRIBUTE_ADDRESSES = asList(
            new AttributeAddress(CLASS_ID, OBIS_CODE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
            new AttributeAddress(CLASS_ID, OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
            new AttributeAddress(CLASS_ID, OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
            new AttributeAddress(CLASS_ID, OBIS_CODE_MBUS_DRIVER_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID));

    private static final AttributeAddress[] FOR_DSMR_4_2_2 = ALL_ATTRIBUTE_ADDRESSES.subList(0, 3)
            .toArray(new AttributeAddress[3]);
    private static final AttributeAddress[] FOR_SMR_5_1 = ALL_ATTRIBUTE_ADDRESSES.subList(0, 4)
            .toArray(new AttributeAddress[4]);

    private DlmsHelperService dlmsHelperService;

    @Autowired
    public GetFirmwareVersionsCommandExecutor(final DlmsHelperService dlmsHelperService) {
        super(GetFirmwareVersionRequestDto.class);
        this.dlmsHelperService = dlmsHelperService;
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
    public List<FirmwareVersionDto> execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Void useless) throws ProtocolAdapterException {
        if (isAnSmr51Device(device)) {
            return getFirmwareVersions(conn, device, FOR_SMR_5_1);
        }
        return getFirmwareVersions(conn, device, FOR_DSMR_4_2_2);
    }

    private boolean isAnSmr51Device(DlmsDevice device) {
        return "SMR".equals(device.getProtocol()) && "5.1".equals(device.getProtocolVersion());
    }

    private List<FirmwareVersionDto> getFirmwareVersions(final DlmsConnectionHolder conn, final DlmsDevice device,
            final AttributeAddress[] attributes) throws ProtocolAdapterException {
        conn.getDlmsMessageListener().setDescription("GetFirmwareVersions, retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(attributes));

        final List<GetResult> results = this.dlmsHelperService.getAndCheck(conn, device,
                "retrieve firmware versions", attributes);

        final List<FirmwareVersionDto> firmwareVersionDtos = new ArrayList<>();
        for (int i = 0; i < attributes.length; i++) {
            final FirmwareModuleType firmwareModuleType = FIRMWARE_MODULE_TYPES.get(i);
            final String description = firmwareModuleType.getDescription();
            final String version = this.dlmsHelperService.readString(results.get(i).getResultData(), description);
            firmwareVersionDtos.add(new FirmwareVersionDto(firmwareModuleType, version));
        }
        return firmwareVersionDtos;
    }

}
