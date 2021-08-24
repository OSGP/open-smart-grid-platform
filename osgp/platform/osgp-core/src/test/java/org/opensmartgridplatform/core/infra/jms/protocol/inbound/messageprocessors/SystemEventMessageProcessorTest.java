/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventDto;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
class SystemEventMessageProcessorTest {

  private static final String DEVICE_IDENTIFICATION = "dvc-1";

  @Mock private SystemEventDto systemEventDto;

  @Mock private DomainInfoRepository domainInfoRepository;

  @Mock private DomainInfo domainInfo;

  @Mock private DomainRequestService domainRequestService;

  @InjectMocks private SystemEventMessageProcessor systemEventMessageProcessor;
  private ObjectMessage message;

  @BeforeEach
  public void init() throws JMSException {

    final String correlationUid = "corr-uid-1";
    final String organisationIdentification = "test-org";
    final String ipAddress = "127.0.0.1";
    final String domain = "SMART_METERING";
    final String domainVersion = "1.0";

    final RequestMessage requestMessage =
        new RequestMessage(
            correlationUid,
            organisationIdentification,
            DEVICE_IDENTIFICATION,
            ipAddress,
            this.systemEventDto);

    this.message =
        new ObjectMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.SYSTEM_EVENT.name())
            .withDeviceIdentification(DEVICE_IDENTIFICATION)
            .withObject(requestMessage)
            .build();
    this.message.setStringProperty(Constants.DOMAIN, domain);
    this.message.setStringProperty(Constants.DOMAIN_VERSION, domainVersion);

    when(this.domainInfoRepository.findByDomainAndDomainVersion(domain, domainVersion))
        .thenReturn(this.domainInfo);
    doNothing()
        .when(this.domainRequestService)
        .send(any(RequestMessage.class), any(String.class), any(DomainInfo.class));
  }

  @Test
  void testProcessMessageSuccess() throws JMSException {

    this.systemEventMessageProcessor.processMessage(this.message);

    verify(this.domainRequestService)
        .send(
            any(RequestMessage.class),
            eq(DeviceFunction.SYSTEM_EVENT.name()),
            any(DomainInfo.class));
  }
}
