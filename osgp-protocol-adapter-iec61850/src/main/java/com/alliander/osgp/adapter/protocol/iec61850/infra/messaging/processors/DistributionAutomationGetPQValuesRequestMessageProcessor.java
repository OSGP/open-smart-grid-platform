/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.processors;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.requests.GetDataDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.GetDataDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.RtuDeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataRequestDto;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataResponseDto;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
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
import org.osgpfoundation.osgp.dto.da.GetDeviceModelRequestDto;
import org.osgpfoundation.osgp.dto.da.GetDeviceModelResponseDto;
import org.osgpfoundation.osgp.dto.da.iec61850.DataSampleDto;
import org.osgpfoundation.osgp.dto.da.iec61850.LogicalDeviceDto;
import org.osgpfoundation.osgp.dto.da.iec61850.LogicalNodeDto;
import org.osgpfoundation.osgp.dto.da.iec61850.PhysicalDeviceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgpfoundation.osgp.dto.da.GetPQValuesRequestDto;
import org.osgpfoundation.osgp.dto.da.GetPQValuesResponseDto;

/**
 * Class for processing distribution automation get pq values request messages
 */
@Component("iec61850DistributionAutomationGetPQValuesRequestMessageProcessor")
public class DistributionAutomationGetPQValuesRequestMessageProcessor extends RtuDeviceRequestMessageProcessor {
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

        this.deviceService.getDataOnly(deviceRequest, iec61850DeviceResponseHandler);
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
        GetDataResponseDto dataResponse = null;

        try {
            final GetDataDeviceResponse response = (GetDataDeviceResponse) deviceResponse;
            dataResponse = response.getDataResponse();
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.PROTOCOL_IEC61850,
                    "Unexpected exception while retrieving response message", e);
        }

        // Transfer GetDataResponseDto to GetDeviceModelResponseDto;
        double health = 0.0d;
        List<GetDataSystemIdentifierDto> getDataSystemIdentifiers = dataResponse.getGetDataSystemIdentifiers();
        for (GetDataSystemIdentifierDto getDataSystemIdentifier : getDataSystemIdentifiers) {
            List<MeasurementDto> measurements = getDataSystemIdentifier.getMeasurements();
            for (MeasurementDto measurement : measurements) {
                health = measurement.getValue();
            }
        }
        String healthStatusType = "NOT_OK";
        if (health >0.95d && health < 1.1d) {
            healthStatusType = "OK";
        }
        List<DataSampleDto> data = new ArrayList<DataSampleDto>();
        DataSampleDto sample = new DataSampleDto("dummy", new Date(), BigDecimal.ONE);
        data.add(sample);
        List<LogicalNodeDto> logicalNodes = new ArrayList<LogicalNodeDto>();
        LogicalNodeDto logicalNode = new LogicalNodeDto("nodeName", data);
        logicalNodes.add(logicalNode);
        List<LogicalDeviceDto> logicalDevices = new ArrayList<LogicalDeviceDto>();
        LogicalDeviceDto logicalDevice = new LogicalDeviceDto("logicalDeviceName", logicalNodes);
        logicalDevices.add(logicalDevice);
        final GetPQValuesResponseDto pqValuesResponseDto = new GetPQValuesResponseDto(logicalDevices);

        final DeviceMessageMetadata deviceMessageMetaData = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, 0);
        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage.Builder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetaData).result(result)
                .osgpException(osgpException).dataObject(pqValuesResponseDto).retryCount(retryCount).build();

        responseMessageSender.send(responseMessage);
    }
}
