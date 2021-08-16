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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.opensmartgridplatform.core.application.services.EventNotificationMessageService;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
class SystemEventMessageProcessorTest {

  private static final String DEVICE_IDENTIFICATION = "dvc-1";

  @Mock private SystemEventDto systemEventDto;

  @Mock private DeviceRepository deviceRepository;

  @Mock private EventNotificationMessageService eventNotificationMessageService;

  @Mock private DeviceAuthorizationRepository deviceAuthorizationRepository;

  @Mock private DeviceAuthorization deviceAuthorization;

  @Mock private Organisation organisation;

  @Mock private DomainInfoRepository domainInfoRepository;

  @Mock private DomainInfo domainInfo;

  @Mock private DomainRequestService domainRequestService;

  @InjectMocks private SystemEventMessageProcessor systemEventMessageProcessor;
  private ObjectMessage message;
  private Device device;

  @BeforeEach
  public void init() throws JMSException, UnknownEntityException {

    final String correlationUid = "corr-uid-1";
    final String organisationIdentification = "test-org";
    final String ipAddress = "127.0.0.1";

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

    this.device = new Device(DEVICE_IDENTIFICATION);

    when(this.deviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(this.device);
    when(this.deviceRepository.save(this.device))
        .thenAnswer((Answer<Void>) invocationOnMock -> null);
    doNothing()
        .when(this.eventNotificationMessageService)
        .handleEvent(
            any(String.class),
            any(Date.class),
            any(EventType.class),
            any(String.class),
            any(Integer.class));
    when(this.deviceAuthorizationRepository.findByDeviceAndFunctionGroup(
            this.device, DeviceFunctionGroup.OWNER))
        .thenReturn(Collections.singletonList(this.deviceAuthorization));
    when(this.deviceAuthorization.getOrganisation()).thenReturn(this.organisation);
    when(this.organisation.getOrganisationIdentification())
        .thenReturn(requestMessage.getOrganisationIdentification());
    when(this.domainInfoRepository.findAll())
        .thenReturn(Collections.singletonList(this.domainInfo));
    when(this.domainInfo.getDomain()).thenReturn("SMART_METERING");
    when(this.domainInfo.getDomainVersion()).thenReturn("1.0");
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

  @Test
  void testUnknownDevice() {
    reset(this.organisation);
    reset(this.deviceAuthorizationRepository);
    reset(this.deviceAuthorization);
    reset(this.deviceRepository);
    reset(this.eventNotificationMessageService);
    reset(this.domainInfoRepository);
    reset(this.domainInfo);
    reset(this.domainRequestService);
    when(this.deviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION)).thenReturn(null);
    assertThatExceptionOfType(JMSException.class)
        .isThrownBy(
            () -> {
              this.systemEventMessageProcessor.processMessage(this.message);
            });
  }

  @Test
  void testUnknownDeviceAuthorization() {
    when(this.deviceAuthorizationRepository.findByDeviceAndFunctionGroup(
            this.device, DeviceFunctionGroup.OWNER))
        .thenReturn(null);
    assertThatExceptionOfType(JMSException.class)
        .isThrownBy(
            () -> {
              this.systemEventMessageProcessor.processMessage(this.message);
            });
  }
}
