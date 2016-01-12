/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnit;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnitQuery;

@Component()
public class GetScalerUnitCommandExecutor implements CommandExecutor<ScalerUnitQuery, ScalerUnit> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetScalerUnitCommandExecutor.class);

    private static final ObisCode REGISTER_FOR_SCALER_UNIT = new ObisCode("1.0.1.8.0.255");
    private static final int CLASS_ID = 3;
    private static final int CLASS_ID_MBUS = 4;
    private static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;
    private static final ObisCode OBIS_CODE_MBUS_MASTER_SCALER_UNIT_1 = new ObisCode("0.1.24.2.1.255");
    private static final ObisCode OBIS_CODE_MBUS_MASTER_SCALER_UNIT_2 = new ObisCode("0.2.24.2.1.255");
    private static final ObisCode OBIS_CODE_MBUS_MASTER_SCALER_UNIT_3 = new ObisCode("0.3.24.2.1.255");
    private static final ObisCode OBIS_CODE_MBUS_MASTER_SCALER_UNIT_4 = new ObisCode("0.4.24.2.1.255");

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public ScalerUnit execute(final LnClientConnection conn, final ScalerUnitQuery scalerUnitQuery)
            throws IOException, TimeoutException, ProtocolAdapterException {

        final ObisCode obisCodeRegister = scalerUnitQuery.getChannel() > 0
                ? this.registerForScalerUnit(scalerUnitQuery.getChannel()) : REGISTER_FOR_SCALER_UNIT;

        LOGGER.debug("Retrieving register for ObisCode: {}", obisCodeRegister);

        final AttributeAddress attrScalerUnit = scalerUnitQuery.getChannel() > 0
                ? new AttributeAddress(CLASS_ID_MBUS, obisCodeRegister, ATTRIBUTE_ID_SCALER_UNIT)
                : new AttributeAddress(CLASS_ID, obisCodeRegister, ATTRIBUTE_ID_SCALER_UNIT);

        final List<GetResult> getResultList = conn.get(attrScalerUnit);

        checkResultList(getResultList);

        GetResult getResult = getResultList.get(0);
        AccessResultCode resultCode = getResult.resultCode();
        LOGGER.debug("AccessResultCode: {}", resultCode.name());
        final DataObject scaler_unit = getResult.resultData();
        LOGGER.debug(this.dlmsHelperService.getDebugInfo(scaler_unit));
        if (!scaler_unit.isComplex()) {
            throw new ProtocolAdapterException("complex data (structure) expected while retrieving scaler and unit.");
        }
        List<DataObject> value = scaler_unit.value();
        if (value.size() != 2) {
            throw new ProtocolAdapterException("expected 2 values while retrieving scaler and unit.");
        }
        final DataObject scaler = value.get(0);
        final DataObject unit = value.get(1);

        return new ScalerUnit(DlmsUnit.fromDlmsEnum(dlmsHelperService.readLongNotNull(unit, "unit value").intValue()),
                dlmsHelperService.readLongNotNull(scaler, "scaler value").intValue());
    }

    private static void checkResultList(final List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving scaler and unit.");
        }

        if (getResultList.size() != 1) {
            LOGGER.info("Expected 1 GetResult while retrieving scaler and unit, got " + getResultList.size());
        }
    }

    private ObisCode registerForScalerUnit(final int channel) throws ProtocolAdapterException {
        switch (channel) {
        case 1:
            return OBIS_CODE_MBUS_MASTER_SCALER_UNIT_1;
        case 2:
            return OBIS_CODE_MBUS_MASTER_SCALER_UNIT_2;
        case 3:
            return OBIS_CODE_MBUS_MASTER_SCALER_UNIT_3;
        case 4:
            return OBIS_CODE_MBUS_MASTER_SCALER_UNIT_4;
        default:
            throw new ProtocolAdapterException(String.format("channel %s not supported", channel));
        }
    }

}
