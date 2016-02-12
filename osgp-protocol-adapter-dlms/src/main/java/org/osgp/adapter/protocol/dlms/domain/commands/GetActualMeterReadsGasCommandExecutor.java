/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.Channel;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime;

@Component()
public class GetActualMeterReadsGasCommandExecutor extends
        AbstractMeterReadsScalerUnitCommandExecutor<ActualMeterReadsQuery, ActualMeterReadsGas> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetActualMeterReadsGasCommandExecutor.class);

    private static final int CLASS_ID_MBUS = 4;
    private static final byte ATTRIBUTE_ID_VALUE = 2;
    private static final byte ATTRIBUTE_ID_TIME = 5;
    private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_1 = new ObisCode("0.1.24.2.1.255");
    private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_2 = new ObisCode("0.2.24.2.1.255");
    private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_3 = new ObisCode("0.3.24.2.1.255");
    private static final ObisCode OBIS_CODE_MBUS_MASTER_VALUE_4 = new ObisCode("0.4.24.2.1.255");

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public ActualMeterReadsGas execute(final LnClientConnection conn, final DlmsDevice device,
            final ActualMeterReadsQuery actualMeterReadsRequest) throws IOException, TimeoutException,
            ProtocolAdapterException {

        final ObisCode obisCodeMbusMasterValue = this.masterValueForChannel(actualMeterReadsRequest.getChannel());

        LOGGER.debug("Retrieving current MBUS master value for ObisCode: {}", obisCodeMbusMasterValue);

        final AttributeAddress mbusValue = new AttributeAddress(CLASS_ID_MBUS,
                this.masterValueForChannel(actualMeterReadsRequest.getChannel()), ATTRIBUTE_ID_VALUE);

        LOGGER.debug("Retrieving current MBUS master capture time for ObisCode: {}", obisCodeMbusMasterValue);

        final AttributeAddress mbusTime = new AttributeAddress(CLASS_ID_MBUS, obisCodeMbusMasterValue,
                ATTRIBUTE_ID_TIME);

        final List<GetResult> getResultList = this.dlmsHelperService.getWithList(conn, device, mbusValue, mbusTime,
                this.getScalerUnitAttributeAddress(actualMeterReadsRequest));

        checkResultList(getResultList);

        final long consumption = this.dlmsHelperService.readLong(getResultList.get(0), "gas consumption");
        final DataObject time = this.dlmsHelperService.readDataObject(getResultList.get(1), "captureTime gas");
        final CosemDateTime cosemDateTime = this.dlmsHelperService.fromDateTimeValue((byte[]) time.value());
        final Date captureTime;
        if (cosemDateTime.isDateTimeSpecified()) {
            captureTime = cosemDateTime.asDateTime().toDate();
        } else {
            throw new ProtocolAdapterException("Unexpected null/unspecified value for M-Bus Capture Time");
        }
        final DataObject scalerUnit = this.dlmsHelperService.readDataObject(getResultList.get(2), "Scaler and Unit");

        return new ActualMeterReadsGas(new Date(), consumption, captureTime, this.convert(scalerUnit));

    }

    private static void checkResultList(final List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException(
                    "No GetResult received while retrieving current MBUS master capture time.");
        }

        if (getResultList.size() != 2) {
            LOGGER.info("Expected 2 GetResult while retrieving current MBUS master capture time, got "
                    + getResultList.size());
        }
    }

    private ObisCode masterValueForChannel(final Channel channel) throws ProtocolAdapterException {
        switch (channel) {
        case ONE:
            return OBIS_CODE_MBUS_MASTER_VALUE_1;
        case TWO:
            return OBIS_CODE_MBUS_MASTER_VALUE_2;
        case THREE:
            return OBIS_CODE_MBUS_MASTER_VALUE_3;
        case FOUR:
            return OBIS_CODE_MBUS_MASTER_VALUE_4;
        default:
            throw new ProtocolAdapterException(String.format("channel %s not supported", channel));
        }
    }

}
