/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.processors;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.openiec61850.LogicalDevice;
import org.openmuc.openiec61850.LogicalNode;
import org.openmuc.openiec61850.ModelNode;
import org.openmuc.openiec61850.ServerModel;
import org.osgpfoundation.osgp.dto.da.GetDeviceModelResponseDto;
import org.osgpfoundation.osgp.dto.da.iec61850.LogicalDeviceDto;
import org.osgpfoundation.osgp.dto.da.iec61850.LogicalNodeDto;
import org.osgpfoundation.osgp.dto.da.iec61850.PhysicalDeviceDto;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.device.da.rtu.DaDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DaRtuDeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;

/**
 * Class for processing distribution automation get device model request
 * messages
 */
@Component("iec61850DistributionAutomationGetDeviceModelRequestMessageProcessor")
public class DistributionAutomationGetDeviceModelRequestMessageProcessor extends DaRtuDeviceRequestMessageProcessor {
    public DistributionAutomationGetDeviceModelRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_DEVICE_MODEL);
    }

    @Override
    public Function<GetDeviceModelResponseDto> getDataFunction(final Iec61850Client client,
            final DeviceConnection connection, final DaDeviceRequest deviceRequest) {
        return (final DeviceMessageLog deviceMessageLog) -> {
            final ServerModel serverModel = connection.getConnection().getServerModel();
            return new GetDeviceModelResponseDto(new PhysicalDeviceDto(connection.getDeviceIdentification(),
                    this.processLogicalDevices(serverModel)));
        };
    }

    private synchronized List<LogicalDeviceDto> processLogicalDevices(final ServerModel model) {
        final List<LogicalDeviceDto> logicalDevices = new ArrayList<>();
        for (final ModelNode node : model.getChildren()) {
            if (node instanceof LogicalDevice) {
                final List<LogicalNodeDto> logicalNodes = this.processLogicalNodes((LogicalDevice) node);
                logicalDevices.add(new LogicalDeviceDto(node.getName(), logicalNodes));
            }
        }
        return logicalDevices;
    }

    private List<LogicalNodeDto> processLogicalNodes(final LogicalDevice node) {
        final List<LogicalNodeDto> logicalNodes = new ArrayList<>();
        for (final ModelNode subNode : node.getChildren()) {
            if (subNode instanceof LogicalNode) {
                logicalNodes.add(new LogicalNodeDto(subNode.getName(), new ArrayList<>()));
            }
        }
        return logicalNodes;
    }
}
