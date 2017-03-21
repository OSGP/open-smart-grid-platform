/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.ClockAttribute;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;

import ma.glasnost.orika.MapperFacade;

@Component
public class SetClockConfigurationCommandExecutor
        extends AbstractCommandExecutor<SetClockConfigurationRequestDto, List<AccessResultCode>> {

    private static final ObisCode LOGICAL_NAME = new ObisCode("0.0.1.0.0.255");

    private static final AttributeAddress ATTRIBUTE_TIME_ZONE = new AttributeAddress(InterfaceClass.CLOCK.id(),
            LOGICAL_NAME, ClockAttribute.TIME_ZONE.attributeId());

    private static final AttributeAddress ATTRIBUTE_DAYLIGHT_SAVINGS_BEGIN = new AttributeAddress(
            InterfaceClass.CLOCK.id(), LOGICAL_NAME, ClockAttribute.DAYLIGHT_SAVINGS_BEGIN.attributeId());

    private static final AttributeAddress ATTRIBUTE_DAYLIGHT_SAVINGS_END = new AttributeAddress(
            InterfaceClass.CLOCK.id(), LOGICAL_NAME, ClockAttribute.DAYLIGHT_SAVINGS_END.attributeId());

    private static final AttributeAddress ATTRIBUTE_DAYLIGHT_SAVINGS_DEVIATION = new AttributeAddress(
            InterfaceClass.CLOCK.id(), LOGICAL_NAME, ClockAttribute.DAYLIGHT_SAVINGS_DEVIATION.attributeId());

    private static final AttributeAddress ATTRIBUTE_DAYLIGHT_SAVINGS_ENABLED = new AttributeAddress(
            InterfaceClass.CLOCK.id(), LOGICAL_NAME, ClockAttribute.DAYLIGHT_SAVINGS_ENABLED.attributeId());

    @Autowired
    private MapperFacade configurationMapper;

    public SetClockConfigurationCommandExecutor() {
        super(SetClockConfigurationRequestDto.class);
    }

    @Override
    public ActionResponseDto asBundleResponse(final List<AccessResultCode> executionResult)
            throws ProtocolAdapterException {
        return new ActionResponseDto("Set clock configuration was successful");
    }

    @Override
    public List<AccessResultCode> execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final SetClockConfigurationRequestDto object) throws ProtocolAdapterException {

        // Create parameters.
        final List<SetParameter> parameters = new ArrayList<>();

        parameters.add(new SetParameter(ATTRIBUTE_TIME_ZONE, DataObject.newInteger16Data(object.getTimeZoneOffset())));
        this.dlmsLogWrite(conn, ATTRIBUTE_TIME_ZONE);

        final CosemDateTime daylightSavingsBegin = this.configurationMapper.map(object.getDaylightSavingsBegin(),
                CosemDateTime.class);
        parameters.add(new SetParameter(ATTRIBUTE_DAYLIGHT_SAVINGS_BEGIN,
                DataObject.newOctetStringData(daylightSavingsBegin.encode())));
        this.dlmsLogWrite(conn, ATTRIBUTE_DAYLIGHT_SAVINGS_BEGIN);

        final CosemDateTime daylightSavingsEnd = this.configurationMapper.map(object.getDaylightSavingsEnd(),
                CosemDateTime.class);
        parameters.add(new SetParameter(ATTRIBUTE_DAYLIGHT_SAVINGS_END,
                DataObject.newOctetStringData(daylightSavingsEnd.encode())));
        this.dlmsLogWrite(conn, ATTRIBUTE_DAYLIGHT_SAVINGS_END);

        parameters.add(new SetParameter(ATTRIBUTE_DAYLIGHT_SAVINGS_DEVIATION,
                DataObject.newInteger8Data(object.getDaylightSavingsDeviation())));
        this.dlmsLogWrite(conn, ATTRIBUTE_DAYLIGHT_SAVINGS_DEVIATION);

        parameters.add(new SetParameter(ATTRIBUTE_DAYLIGHT_SAVINGS_ENABLED,
                DataObject.newBoolData(object.isDaylightSavingsEnabled())));
        this.dlmsLogWrite(conn, ATTRIBUTE_DAYLIGHT_SAVINGS_ENABLED);

        try {
            final List<AccessResultCode> results = conn.getConnection().set(parameters);

            for (final AccessResultCode result : results) {
                if (!result.equals(AccessResultCode.SUCCESS)) {
                    throw new ProtocolAdapterException(
                            "Not all attributes of the clock configuration could be set successfully.");
                }
            }

            return results;
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    private void dlmsLogWrite(final DlmsConnectionHolder conn, final AttributeAddress attribute) {
        conn.getDlmsMessageListener().setDescription("SetClockConfiguration, preparing to write attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attribute));
    }
}
