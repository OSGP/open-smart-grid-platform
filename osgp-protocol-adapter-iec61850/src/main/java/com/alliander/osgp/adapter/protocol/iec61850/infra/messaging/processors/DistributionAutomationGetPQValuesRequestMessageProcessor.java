/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.processors;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.da.rtu.DaDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.requests.GetDataDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DaRtuDeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataRequestDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SystemFilterDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;
import org.openmuc.openiec61850.BdaFloat32;
import org.openmuc.openiec61850.BdaQuality;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.ConstructedDataAttribute;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.LogicalDevice;
import org.openmuc.openiec61850.LogicalNode;
import org.openmuc.openiec61850.ModelNode;
import org.openmuc.openiec61850.ServerModel;
import org.osgpfoundation.osgp.dto.da.iec61850.DataSampleDto;
import org.osgpfoundation.osgp.dto.da.iec61850.LogicalDeviceDto;
import org.osgpfoundation.osgp.dto.da.iec61850.LogicalNodeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgpfoundation.osgp.dto.da.GetPQValuesRequestDto;
import org.osgpfoundation.osgp.dto.da.GetPQValuesResponseDto;

/**
 * Class for processing distribution automation get pq values request messages
 */
@Component("iec61850DistributionAutomationGetPQValuesRequestMessageProcessor")
public class DistributionAutomationGetPQValuesRequestMessageProcessor extends DaRtuDeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributionAutomationGetPQValuesRequestMessageProcessor.class);

    public DistributionAutomationGetPQValuesRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_POWER_QUALITY_VALUES);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing distribution automation get device model request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String ipAddress = null;
        int retryCount = 0;
        boolean isScheduled = false;
        GetPQValuesRequestDto getPQValuesRequest = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            domain = message.getStringProperty(Constants.DOMAIN);
            domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            ipAddress = message.getStringProperty(Constants.IP_ADDRESS);
            retryCount = message.getIntProperty(Constants.RETRY_COUNT);
            isScheduled = message.propertyExists(Constants.IS_SCHEDULED)
                    ? message.getBooleanProperty(Constants.IS_SCHEDULED) : false;
            getPQValuesRequest = (GetPQValuesRequestDto) message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("domain: {}", domain);
            LOGGER.debug("domainVersion: {}", domainVersion);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("ipAddress: {}", ipAddress);
            return;
        }

        final RequestMessageData requestMessageData = new RequestMessageData(null, domain, domainVersion, messageType,
                retryCount, isScheduled, correlationUid, organisationIdentification, deviceIdentification);

        this.printDomainInfo(messageType, domain, domainVersion);

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this
                .createIec61850DeviceResponseHandler(requestMessageData, message);

        // transform GetDeviceModelRequestDto to GetDataRequestDto

        final List<MeasurementFilterDto> measurementFilters = new ArrayList<MeasurementFilterDto>();
        MeasurementFilterDto measurementFilterDto = new MeasurementFilterDto(1, "Health", true);
        measurementFilters.add(measurementFilterDto);
        final List<SystemFilterDto> systemFilters = new ArrayList<SystemFilterDto>();
        SystemFilterDto systemFilterDto = new SystemFilterDto(1, "RTU", measurementFilters, true);
        systemFilters.add(systemFilterDto);
        final GetDataRequestDto getDataRequest = new GetDataRequestDto(systemFilters);

        final GetDataDeviceRequest deviceRequest = new GetDataDeviceRequest(organisationIdentification,
                deviceIdentification, correlationUid, getDataRequest, domain, domainVersion, messageType, ipAddress,
                retryCount, isScheduled);

        this.deviceService.getData(deviceRequest, iec61850DeviceResponseHandler, this);
    }

    public Function<GetPQValuesResponseDto> getDataFunction(DeviceConnection connection, GetDataDeviceRequest deviceRequest) {
        final Function<GetPQValuesResponseDto> function = new Function<GetPQValuesResponseDto>() {

            @Override
            public GetPQValuesResponseDto apply() throws Exception {
                ServerModel serverModel = connection.getConnection().getServerModel();
                final GetPQValuesResponseDto pqValuesResponseDto = new GetPQValuesResponseDto(processPQValuesLogicalDevice(serverModel));
                return pqValuesResponseDto;
            }
        };

        return function;
    };

    private synchronized List<LogicalDeviceDto> processPQValuesLogicalDevice(ServerModel model) {
        List<LogicalDeviceDto> logicalDevices = new ArrayList<LogicalDeviceDto>();
        for (ModelNode node : model.getChildren()) {
            if (node instanceof LogicalDevice) {
                List<LogicalNodeDto> logicalNodes = processPQValuesLogicalNodes((LogicalDevice) node);
                if (logicalNodes.size()>0) {
                    logicalDevices.add(new LogicalDeviceDto(node.getName(), logicalNodes));
                }
            }
        }
        return logicalDevices;
    }

    private List<LogicalNodeDto> processPQValuesLogicalNodes(LogicalDevice node) {
        List<LogicalNodeDto> logicalNodes = new ArrayList<LogicalNodeDto>();
        for (ModelNode subNode : node.getChildren()) {
            if (subNode instanceof LogicalNode) {
                List<DataSampleDto> data = processPQValueNodeChildren((LogicalNode) subNode);
                if (data.size()>0) {
                    logicalNodes.add(new LogicalNodeDto(subNode.getName(), data));
                }
            }
        }
        return logicalNodes;
    }

    private List<DataSampleDto> processPQValueNodeChildren(LogicalNode node) {
        List<DataSampleDto> data = new ArrayList<DataSampleDto>();
        Collection<ModelNode> children = node.getChildren();
        Map<String, Set<Fc>> childMap = new HashMap<>();
        for (ModelNode child : children) {
            if (!childMap.containsKey(child.getName())) {
                childMap.put(child.getName(), new HashSet<Fc>());
            }
            childMap.get(child.getName()).add(((FcModelNode) child).getFc());
        }
        for (Map.Entry<String, Set<Fc>> childEntry : childMap.entrySet()) {
            List<DataSampleDto> childData = processPQValuesFunctionalConstraintObject( node, childEntry.getKey(), childEntry.getValue());
            if (childData.size()>0) {
                data.addAll(childData);
            }
        }
        return data;
    }

    private List<DataSampleDto> processPQValuesFunctionalConstraintObject(LogicalNode parentNode, String childName,
                                                           Set<Fc> childFcs) {
        List<DataSampleDto> data = new ArrayList<DataSampleDto>();
        for (Fc constraint : childFcs) {
            List<DataSampleDto> childData = processPQValuesFunctionalChildConstraintObject(parentNode, childName, constraint);
            if (childData.size()>0) {
                data.addAll(childData);
            }
        }
        return data;
    }

    private List<DataSampleDto> processPQValuesFunctionalChildConstraintObject(LogicalNode parentNode, String childName, Fc constraint) {
        List<DataSampleDto> data = new ArrayList<DataSampleDto>();
        ModelNode node = parentNode.getChild(childName, constraint);
        if (Fc.MX == constraint && node.getChildren()!=null) {
            if (nodeHasBdaQualityChild(node)) {
                data.add(processPQValue(node));
            } else {
                for (ModelNode subNode : node.getChildren()) {
                    data.add(processPQValue(node, subNode));
                }
            }
        }
        return data;
    }

    private boolean nodeHasBdaQualityChild(ModelNode node) {
        for (ModelNode subNode : node.getChildren()) {
            if (subNode instanceof BdaQuality) {
                return true;
            }
        }
        return false;
    }

    private DataSampleDto processPQValue(ModelNode node) {
        Date ts = null;
        String type = null;
        BigDecimal value = null;
        if (node.getChildren() != null) {
            for (ModelNode subNode : node.getChildren()) {
                if (subNode instanceof BdaQuality) {
//                 For now we do not use Quality
                } else if (subNode instanceof BdaTimestamp) {
                    ts = ((BdaTimestamp) subNode).getDate();
                } else if (subNode instanceof ConstructedDataAttribute) {
                    if (subNode.getChildren()!=null) {
                        for (ModelNode subSubNode : subNode.getChildren()) {
                            if (subSubNode instanceof BdaFloat32) {
                                type = node.getName() + "." + subNode.getName() + "." + subSubNode.getName();
                                value = new BigDecimal(((BdaFloat32) subSubNode).getFloat(),
                                        new MathContext(3, RoundingMode.HALF_EVEN));
                            }
                        }
                    }
                }
            }
        }
        DataSampleDto sample = new DataSampleDto(type, ts, value);
        return sample;
    }

    private DataSampleDto processPQValue(ModelNode parentNode, ModelNode node) {
        Date ts = null;
        String type = null;
        BigDecimal value = null;
        if (node.getChildren() != null) {
            for (ModelNode subNode : node.getChildren()) {
                if (subNode instanceof BdaQuality) {
//                 For now we do not use Quality
                } else if (subNode instanceof BdaTimestamp) {
                    ts = ((BdaTimestamp) subNode).getDate();
                } else if (subNode instanceof ConstructedDataAttribute) {
                    if (subNode.getChildren()!=null) {
                        for (ModelNode subSubNode : subNode.getChildren()) {
                            if (subSubNode instanceof ConstructedDataAttribute) {
                                if (subSubNode.getChildren()!=null) {
                                    for (ModelNode subSubSubNode : subSubNode.getChildren()) {
                                        if (subSubSubNode instanceof BdaFloat32) {
                                            type = parentNode.getName() + "." + node.getName() + "." + subNode.getName() + "." + subSubNode.getName() + "." + subSubSubNode.getName();

                                            value = new BigDecimal(((BdaFloat32) subSubSubNode).getFloat(),
                                                    new MathContext(3, RoundingMode.HALF_EVEN));
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        DataSampleDto sample = new DataSampleDto(type, ts, value);
        return sample;
    }





    @Override
    public void handleDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {
        LOGGER.info("Override for handleDeviceResponse() by DistributionAutomationGetDeviceModelRequestMessageProcessor");
        this.handleGetDataDeviceResponse(deviceResponse, responseMessageSender, domain, domainVersion, messageType,
                retryCount);
    }

    private void handleGetDataDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = null;
        GetPQValuesResponseDto pqValuesResponseDto  = null;

        try {
            final DaDeviceResponse response = (DaDeviceResponse) deviceResponse;
            pqValuesResponseDto = (GetPQValuesResponseDto) response.getDataResponse();
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.PROTOCOL_IEC61850,
                    "Unexpected exception while retrieving response message", e);
        }

        final DeviceMessageMetadata deviceMessageMetaData = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, 0);
        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage.Builder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetaData).result(result)
                .osgpException(osgpException).dataObject(pqValuesResponseDto).retryCount(retryCount).build();

        responseMessageSender.send(responseMessage);
    }
}
