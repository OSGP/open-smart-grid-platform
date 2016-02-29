/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.io.Serializable;
import java.util.List;

import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveConfigurationObjectsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SynchronizeTimeCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.osgp.adapter.protocol.dlms.infra.ws.JasperWirelessSmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetails;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequest;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.jasperwireless.api.ws.service.GetSMSDetailsResponse;
import com.jasperwireless.api.ws.service.SendSMSResponse;
import com.jasperwireless.api.ws.service.SmsMessageType;

@Service(value = "dlmsAdhocService")
public class AdhocService extends DlmsApplicationService {
    private static final String COMMUNICATION_METHOD_GPRS = "GPRS";

    private static final Logger LOGGER = LoggerFactory.getLogger(AdhocService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    @Autowired
    private SynchronizeTimeCommandExecutor synchronizeTimeCommandExecutor;

    @Autowired
    private RetrieveConfigurationObjectsCommandExecutor retrieveConfigurationObjectsCommandExecutor;

    @Autowired
    private JasperWirelessSmsClient smsClient;

    // === REQUEST Synchronize Time DATA ===

    public void synchronizeTime(final DlmsDeviceMessageMetadata messageMetadata,
            final SynchronizeTimeRequest synchronizeTimeRequest, final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "synchronizeTime");

        LnClientConnection conn = null;
        try {

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            conn = this.dlmsConnectionFactory.getConnection(device);

            this.synchronizeTimeCommandExecutor.execute(conn, device, null);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during synchronizeTime", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    synchronizeTimeRequest);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    // === REQUEST Send Wakeup SMS ===

    public void sendWakeUpSms(final DlmsDeviceMessageMetadata messageMetadata,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "sendWakeUpSms");

        try {

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            if (COMMUNICATION_METHOD_GPRS.equals(device.getCommunicationMethod())) {
                final SendSMSResponse response = this.smsClient.sendWakeUpSMS(device.getIccId());
                final SmsDetails smsDetails = new SmsDetails(device.getDeviceIdentification(), response.getSmsMsgId(),
                        null, null, null);
                this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                        smsDetails);
            } else {
                final OsgpException oex = new OsgpException(ComponentType.PROTOCOL_DLMS,
                        "Device communication method is not GPRS");

                this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, oex, responseMessageSender,
                        "");
            }

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during Send Wakeup SMS", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, "");
        }
    }

    // === REQUEST Get SMS Details ===

    public void getSmsDetails(final DlmsDeviceMessageMetadata messageMetadata, final SmsDetails smsDetailsRequest,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "synchronizeTime");

        try {

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            final GetSMSDetailsResponse response = this.smsClient.getSMSDetails(smsDetailsRequest.getSmsMsgId(),
                    device.getIccId());

            SmsDetails smsDetailsResponse = null;
            final List<SmsMessageType> smsMessagesTypes = response.getSmsMessages().getSmsMessage();
            for (final SmsMessageType smsMessageType : smsMessagesTypes) {
                if (smsMessageType.getSmsMsgId() == smsDetailsRequest.getSmsMsgId().longValue()) {
                    smsDetailsResponse = new SmsDetails(device.getDeviceIdentification(), smsMessageType.getSmsMsgId(),
                            smsMessageType.getStatus(), smsMessageType.getSmsMsgAttemptStatus(),
                            smsMessageType.getMsgType());
                }
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    smsDetailsResponse);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during Get SMS Details", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    public void retrieveConfigurationObjects(final DlmsDeviceMessageMetadata messageMetadata,
            final RetrieveConfigurationObjectsRequest request, final DeviceResponseMessageSender responseMessageSender) {
        this.logStart(LOGGER, messageMetadata, "retrieveConfigurationObjects");

        LnClientConnection conn = null;
        try {

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            conn = this.dlmsConnectionFactory.getConnection(device);

            final Serializable response = this.retrieveConfigurationObjectsCommandExecutor.execute(conn, device, null);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    response);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during retrieveConfigurationObjects", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    request);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

    }
}
