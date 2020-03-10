/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import java.util.ArrayList;
import java.util.List;

import com.beanit.openiec61850.LogicalDevice;
import com.beanit.openiec61850.LogicalNode;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu.DaDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.DaRtuDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.dto.da.GetDeviceModelResponseDto;
import org.opensmartgridplatform.dto.da.iec61850.LogicalDeviceDto;
import org.opensmartgridplatform.dto.da.iec61850.LogicalNodeDto;
import org.opensmartgridplatform.dto.da.iec61850.PhysicalDeviceDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

/**
 * Class for processing distribution automation get device model request
 * messages
 */
@Component("iec61850DistributionAutomationGetDeviceModelRequestMessageProcessor")
public class DistributionAutomationGetDeviceModelRequestMessageProcessor extends DaRtuDeviceRequestMessageProcessor {
    public DistributionAutomationGetDeviceModelRequestMessageProcessor() {
        super(MessageType.GET_DEVICE_MODEL);
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
