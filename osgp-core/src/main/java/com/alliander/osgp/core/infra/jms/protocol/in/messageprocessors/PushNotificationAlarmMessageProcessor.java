/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.jms.protocol.in.messageprocessors;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.core.domain.model.domain.DomainRequestService;
import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolRequestMessageProcessor;
import com.alliander.osgp.domain.core.entities.DomainInfo;
import com.alliander.osgp.domain.core.repositories.DomainInfoRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushNotificationAlarm;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Component("dlmsPushNotificationAlarmMessageProcessor")
public class PushNotificationAlarmMessageProcessor extends ProtocolRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationAlarmMessageProcessor.class);

    @Autowired
    private DomainRequestService domainRequestService;

    @Autowired
    private DomainInfoRepository domainInfoRepository;

    protected PushNotificationAlarmMessageProcessor() {
        super(DeviceFunction.PUSH_NOTIFICATION_ALARM);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        final String messageType = message.getJMSType();
        final String organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
        final String deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

        LOGGER.info("Received message of messageType: {} organisationIdentification: {} deviceIdentification: {}",
                messageType, organisationIdentification, deviceIdentification);

        final RequestMessage requestMessage = (RequestMessage) message.getObject();
        final Object dataObject = requestMessage.getRequest();

        try {
            final PushNotificationAlarm pushNotificationAlarm = (PushNotificationAlarm) dataObject;

            /*
             * This message processor handles messages that came in on the
             * osgp-core.1_0.protocol-dlms.1_0.requests queue. Therefore lookup
             * the DomainInfo for DLMS (domain: SMART_METERING) version 1.0.
             *
             * At some point in time there may be a cleaner solution, where the
             * DomainInfo can be derived from information in the message or JMS
             * metadata, but for now this will have to do.
             */
            final List<DomainInfo> domainInfos = this.domainInfoRepository.findAll();
            DomainInfo smartMeteringDomain = null;
            for (final DomainInfo di : domainInfos) {
                if ("SMART_METERING".equals(di.getDomain()) && "1.0".equals(di.getDomainVersion())) {
                    smartMeteringDomain = di;
                    break;
                }
            }

            if (smartMeteringDomain == null) {
                LOGGER.error(
                        "No DomainInfo found for SMART_METERING 1.0, unable to send message of message type: {} to domain adapter. RequestMessage for {} dropped.",
                        messageType, pushNotificationAlarm);
            } else {
                this.domainRequestService.send(requestMessage, DeviceFunction.PUSH_NOTIFICATION_ALARM.name(),
                        smartMeteringDomain);
            }

        } catch (final Exception e) {
            LOGGER.error("Exception", e);
            throw new JMSException(e.getMessage());
        }
    }
}
