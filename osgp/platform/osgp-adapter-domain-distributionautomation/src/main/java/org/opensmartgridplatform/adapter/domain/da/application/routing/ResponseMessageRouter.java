/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.routing;

import org.opensmartgridplatform.adapter.domain.da.infra.jms.kafka.KafkaResponseMessageSender;
import org.opensmartgridplatform.adapter.domain.da.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.IntegrationType;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/*
 * Handles the routing of the response message. It sends it to the web service adapter, the kafka adapter or both.
 */
@Component(value = "domainDistributionAutomationOutboundResponseMessageRouter")
public class ResponseMessageRouter implements NotificationResponseMessageSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseMessageRouter.class);

  @Autowired private DeviceDomainService deviceDomainService;

  @Autowired
  @Qualifier(value = "domainDistributionAutomationOutboundWebServiceResponsesMessageSender")
  private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Autowired
  @Qualifier(value = "domainDistributionAutomationOutboundKafkaResponsesMessageSender")
  private KafkaResponseMessageSender kafkaResponseMessageSender;

  @Override
  public void send(final ResponseMessage responseMessage, final String messageType) {

    final IntegrationType integrationType =
        this.getIntegrationType(responseMessage.getDeviceIdentification());

    switch (integrationType) {
      case BOTH:
        this.kafkaResponseMessageSender.send(responseMessage, messageType);
        this.webServiceResponseMessageSender.send(responseMessage, messageType);
        break;
      case KAFKA:
        this.kafkaResponseMessageSender.send(responseMessage, messageType);
        break;
      default:
        this.webServiceResponseMessageSender.send(responseMessage, messageType);
    }
  }

  private IntegrationType getIntegrationType(final String deviceIdentification) {

    try {
      final Device device = this.deviceDomainService.searchDevice(deviceIdentification);
      return device.getIntegrationType();
    } catch (final Exception e) {
      LOGGER.error(
          "Could not determine integration type based on the device; we are using the default value WEB_SERVICE",
          e);
      return IntegrationType.WEB_SERVICE;
    }
  }
}
