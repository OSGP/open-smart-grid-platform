/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import org.joda.time.DateTime;
import com.beanit.openiec61850.Fc;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850MaterialStatusCommand implements RtuReadCommand<MeasurementDto> {

    private final LogicalNode logicalNode;
    private final int index;

    public Iec61850MaterialStatusCommand(final int index) {
        this.logicalNode = LogicalNode.fromString("MFLW" + index);
        this.index = index;
    }

    @Override
    public MeasurementDto execute(final Iec61850Client client, final DeviceConnection connection,
            final LogicalDevice logicalDevice, final int logicalDeviceIndex) throws NodeException {
        final NodeContainer containingNode = connection.getFcModelNode(logicalDevice, logicalDeviceIndex,
                this.logicalNode, DataAttribute.MATERIAL_STATUS, Fc.SP);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return this.translate(containingNode);
    }

    @Override
    public MeasurementDto translate(final NodeContainer containingNode) {
        return new MeasurementDto(this.index, SubDataAttribute.SETPOINT_VALUE.getDescription(), 0, DateTime.now(),
                containingNode.getInteger(SubDataAttribute.SETPOINT_VALUE).getValue());
    }
}
