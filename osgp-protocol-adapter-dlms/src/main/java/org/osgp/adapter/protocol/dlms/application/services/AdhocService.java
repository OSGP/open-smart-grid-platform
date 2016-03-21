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

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveConfigurationObjectsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SynchronizeTimeCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.ws.JasperWirelessSmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.RetrieveConfigurationObjectsRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetailsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.jasperwireless.api.ws.service.GetSMSDetailsResponse;
import com.jasperwireless.api.ws.service.SendSMSResponse;
import com.jasperwireless.api.ws.service.SmsMessageType;

@Service(value = "dlmsAdhocService")
public class AdhocService {
    private static final String COMMUNICATION_METHOD_GPRS = "GPRS";

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

    public void synchronizeTime(final ClientConnection conn, final DlmsDevice device,
            final SynchronizeTimeRequestDto synchronizeTimeRequest) throws ProtocolAdapterException {
        this.synchronizeTimeCommandExecutor.execute(conn, device, null);
    }

    // === REQUEST Send Wakeup SMS ===

    public SmsDetailsDto sendWakeUpSms(final ClientConnection conn, final DlmsDevice device) throws OsgpException {

        if (!COMMUNICATION_METHOD_GPRS.equals(device.getCommunicationMethod())) {
            throw new OsgpException(ComponentType.PROTOCOL_DLMS, "Device communication method is not GPRS");
        }

        final SendSMSResponse response = this.smsClient.sendWakeUpSMS(device.getIccId());
        return new SmsDetailsDto(device.getDeviceIdentification(), response.getSmsMsgId(), null, null, null);
    }

    // === REQUEST Get SMS Details ===

    public SmsDetailsDto getSmsDetails(final ClientConnection conn, final DlmsDevice device,
            final SmsDetailsDto smsDetailsRequest) throws OsgpException {

        final GetSMSDetailsResponse response = this.smsClient.getSMSDetails(smsDetailsRequest.getSmsMsgId(),
                device.getIccId());

        SmsDetailsDto smsDetailsResponse = null;
        final List<SmsMessageType> smsMessagesTypes = response.getSmsMessages().getSmsMessage();
        for (final SmsMessageType smsMessageType : smsMessagesTypes) {
            if (smsMessageType.getSmsMsgId() == smsDetailsRequest.getSmsMsgId().longValue()) {
                smsDetailsResponse = new SmsDetailsDto(device.getDeviceIdentification(), smsMessageType.getSmsMsgId(),
                        smsMessageType.getStatus(), smsMessageType.getSmsMsgAttemptStatus(),
                        smsMessageType.getMsgType());
            }
        }

        return smsDetailsResponse;
    }

    public Serializable retrieveConfigurationObjects(final ClientConnection conn, final DlmsDevice device,
            final RetrieveConfigurationObjectsRequestDto request) throws ProtocolAdapterException {

        return this.retrieveConfigurationObjectsCommandExecutor.execute(conn, device, null);
    }
}
