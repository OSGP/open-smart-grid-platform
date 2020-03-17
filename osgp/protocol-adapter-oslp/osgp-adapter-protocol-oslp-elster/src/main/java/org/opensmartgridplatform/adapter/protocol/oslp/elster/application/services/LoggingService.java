/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.OslpDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpLogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpLogItemRequestMessageSender;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoggingService {

    @Autowired
    private OslpDeviceRepository oslpDeviceRepository;

    @Autowired
    private OslpLogItemRequestMessageSender oslpLogItemRequestMessageSender;

    public void logMessage(final OslpEnvelope message, final boolean incoming) {

        final String deviceUid = Base64.encodeBase64String(message.getDeviceId());
        String deviceIdentification = this.getDeviceIdentificationFromMessage(message.getPayloadMessage());
        if (StringUtils.isEmpty(deviceIdentification)) {
            deviceIdentification = this.getDeviceIdentification(deviceUid);
        }

        // Assume outgoing messages always valid.
        final boolean isValid = !incoming || message.isValid();

        final OslpLogItemRequestMessage oslpLogItemRequestMessage = new OslpLogItemRequestMessage(null, deviceUid,
                deviceIdentification, incoming, isValid, message.getPayloadMessage(), message.getSize());

        this.oslpLogItemRequestMessageSender.send(oslpLogItemRequestMessage);
    }

    private String getDeviceIdentificationFromMessage(final Oslp.Message message) {
        String deviceIdentification = EMPTY;

        if (message.hasRegisterDeviceRequest()) {
            deviceIdentification = message.getRegisterDeviceRequest().getDeviceIdentification();
        }

        return deviceIdentification;
    }

    private String getDeviceIdentification(final String deviceUid) {
        String deviceIdentification = EMPTY;

        final OslpDevice oslpDevice = this.oslpDeviceRepository.findByDeviceUid(deviceUid);
        if (oslpDevice != null) {
            deviceIdentification = oslpDevice.getDeviceIdentification();
        }

        return deviceIdentification;
    }
}
