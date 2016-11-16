/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openmuc.openiec61850.Fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeReadException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.QualityConverter;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850LoadTotalEnergyCommand implements RtuReadCommand<MeasurementDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850LoadActualPowerCommand.class);
    private static final String NODE = "MMTR";

    private LogicalNode logicalNode;
    private int index;

    public Iec61850LoadTotalEnergyCommand(final int index) {
        this.logicalNode = LogicalNode.fromString(NODE + index);
        this.index = index;
    }

    @Override
    public MeasurementDto execute(final Iec61850Client client, final DeviceConnection connection,
            final LogicalDevice logicalDevice) throws NodeReadException {
        final NodeContainer containingNode = connection.getFcModelNode(logicalDevice, this.logicalNode,
                DataAttribute.TOTAL_ENERGY, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return this.translate(containingNode);
    }

    @Override
    public MeasurementDto translate(final NodeContainer containingNode) {
        // Load total energy is implemented different on both RTUs
        // (one uses Int64, the other Int32)
        // As a workaround first try to read the value as Integer
        // If that fails read the value as Long
        long value = 0;
        try {
            value = containingNode.getInteger(SubDataAttribute.ACTUAL_VALUE).getValue();
        } catch (final ClassCastException e) {
            LOGGER.info("Reading integer value resulted in class cast exception, trying to read long value", e);
            value = containingNode.getLong(SubDataAttribute.ACTUAL_VALUE).getValue();
        }

        return new MeasurementDto(this.index, DataAttribute.TOTAL_ENERGY.getDescription(),
                QualityConverter.toShort(containingNode.getQuality(SubDataAttribute.QUALITY).getValue()),
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC), value);
    }
}
