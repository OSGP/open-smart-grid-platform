/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.List;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScanMbusChannelsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Component
public class ScanMbusChannelsCommandExecutor extends AbstractCommandExecutor<Void, ScanMbusChannelsResponseDto> {

    private static final int CLASS_ID = 72;
    private static final ObisCode OBIS_CODE_CHANNEL_1 = new ObisCode("0.1.24.1.0.255");
    private static final ObisCode OBIS_CODE_CHANNEL_2 = new ObisCode("0.2.24.1.0.255");
    private static final ObisCode OBIS_CODE_CHANNEL_3 = new ObisCode("0.3.24.1.0.255");
    private static final ObisCode OBIS_CODE_CHANNEL_4 = new ObisCode("0.4.24.1.0.255");
    private static final int ATTRIBUTE_ID_IDENTIFICATION_NUMBER = 6;

    private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = {
            new AttributeAddress(CLASS_ID, OBIS_CODE_CHANNEL_1, ATTRIBUTE_ID_IDENTIFICATION_NUMBER),
            new AttributeAddress(CLASS_ID, OBIS_CODE_CHANNEL_2, ATTRIBUTE_ID_IDENTIFICATION_NUMBER),
            new AttributeAddress(CLASS_ID, OBIS_CODE_CHANNEL_3, ATTRIBUTE_ID_IDENTIFICATION_NUMBER),
            new AttributeAddress(CLASS_ID, OBIS_CODE_CHANNEL_4, ATTRIBUTE_ID_IDENTIFICATION_NUMBER) };

    private static final int INDEX_CHANNEL_1 = 0;
    private static final int INDEX_CHANNEL_2 = 1;
    private static final int INDEX_CHANNEL_3 = 2;
    private static final int INDEX_CHANNEL_4 = 3;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    public ScanMbusChannelsCommandExecutor() {
        super(ScanMbusChannelsRequestDataDto.class);
    }

    @Override
    public Void fromBundleRequestInput(final ActionRequestDto bundleInput) throws ProtocolAdapterException {
        /*
         * ScanMbusChannelsRequestDto does not contain any values to pass on, and the
         * ScanMbusChannelsCommandExecutor takes a Void as input that is ignored.
         */
        return null;
    }

    @Override
    public ActionResponseDto asBundleResponse(final ScanMbusChannelsResponseDto executionResult)
            throws ProtocolAdapterException {
        return executionResult;
    }

    @Override
    public ScanMbusChannelsResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Void mbusAttributesDto) throws OsgpException {

        conn.getDlmsMessageListener().setDescription("ScanMbusChannels, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(ATTRIBUTE_ADDRESSES));

        final List<GetResult> getResultList = this.dlmsHelperService.getAndCheck(conn, device, "Scan Mbus channels",
                ATTRIBUTE_ADDRESSES);

        return new ScanMbusChannelsResponseDto(
                Long.toHexString(this.dlmsHelperService.readLong(getResultList.get(INDEX_CHANNEL_1).getResultData(),
                        "Mbus channel 1 identification number")),
                Long.toHexString(this.dlmsHelperService.readLong(getResultList.get(INDEX_CHANNEL_2).getResultData(),
                        "Mbus channel 2 identification number")),
                Long.toHexString(this.dlmsHelperService.readLong(getResultList.get(INDEX_CHANNEL_3).getResultData(),
                        "Mbus channel 3 identification number")),
                Long.toHexString(this.dlmsHelperService.readLong(getResultList.get(INDEX_CHANNEL_4).getResultData(),
                        "Mbus channel 4 identification number")));
    }
}
