/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.core.application.mapping.DomainCoreMapper;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.Certification;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.EventNotificationType;
import org.opensmartgridplatform.dto.valueobjects.CertificationDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@ExtendWith(MockitoExtension.class)
public class DeviceManagementServiceTest {

  private static final String TEST_ORGANISATION = "testOrganisation";
  private static final String TEST_DEVICE = "testDevice";
  private static final String TEST_IP = "testIp";
  private static final String TEST_UID = "testUid";
  private static final String TEST_MESSAGE_TYPE = "testMessageType";
  private static final int TEST_PRIORITY = 1;

  @Mock private TransactionalDeviceService transactionalDeviceService;

  @Mock private DeviceDomainService deviceDomainService;

  @Mock private DomainCoreMapper domainCoreMapper;

  @Mock private OsgpCoreRequestMessageSender osgpCoreRequestManager;

  @Mock private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Mock private OrganisationDomainService organisationDomainService;

  @InjectMocks private DeviceManagementService deviceManagementService;

  @Captor private ArgumentCaptor<RequestMessage> argumentRequestMessage;

  @Captor private ArgumentCaptor<ResponseMessage> argumentResponseMessage;

  @Captor private ArgumentCaptor<String> argumentMessageType;

  @Captor private ArgumentCaptor<String> argumentIpAddress;

  @Captor private ArgumentCaptor<String> argumentDeviceIdentification;

  @Captor private ArgumentCaptor<Integer> argumentPriority;

  @BeforeEach
  public void init() throws UnknownEntityException {
    when(this.organisationDomainService.searchOrganisation(any())).thenReturn(new Organisation());
  }

  @Test
  public void testSetEventNotifications() throws FunctionalException {
    final List<EventNotificationType> eventNotifications =
        Arrays.asList(EventNotificationType.COMM_EVENTS, EventNotificationType.DIAG_EVENTS);
    final Device device = mock(Device.class);
    when(device.getIpAddress()).thenReturn(TEST_IP);
    when(this.deviceDomainService.searchActiveDevice(TEST_DEVICE, ComponentType.DOMAIN_CORE))
        .thenReturn(device);

    this.deviceManagementService.setEventNotifications(
        TEST_ORGANISATION,
        TEST_DEVICE,
        TEST_UID,
        eventNotifications,
        TEST_MESSAGE_TYPE,
        TEST_PRIORITY);

    verify(this.osgpCoreRequestManager)
        .send(
            this.argumentRequestMessage.capture(),
            this.argumentMessageType.capture(),
            this.argumentPriority.capture(),
            this.argumentIpAddress.capture());

    final RequestMessage expectedRequestMessage =
        this.createNewRequestMessage(
            new EventNotificationMessageDataContainerDto(
                this.domainCoreMapper.mapAsList(
                    eventNotifications, EventNotificationTypeDto.class)));

    assertThat(this.argumentRequestMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedRequestMessage);
    assertThat(this.argumentMessageType.getValue()).isEqualTo(TEST_MESSAGE_TYPE);
    assertThat(this.argumentPriority.getValue()).isEqualTo(TEST_PRIORITY);
    assertThat(this.argumentIpAddress.getValue()).isEqualTo(TEST_IP);
  }

  @Test
  public void testUpdateDeviceSslCertificationIsNull() throws FunctionalException {
    this.deviceManagementService.updateDeviceSslCertification(
        TEST_ORGANISATION, TEST_DEVICE, TEST_UID, null, TEST_MESSAGE_TYPE, TEST_PRIORITY);

    // This method is not called since it comes after the check of the
    // certificate
    verifyNoInteractions(this.domainCoreMapper);
  }

  @Test
  public void testUpdateDeviceSslCertification() throws FunctionalException {
    final Device device = mock(Device.class);
    when(device.getIpAddress()).thenReturn(TEST_IP);
    when(this.deviceDomainService.searchActiveDevice(TEST_DEVICE, ComponentType.DOMAIN_CORE))
        .thenReturn(device);
    final Certification certification = new Certification("testUrl", "testDomain");

    this.deviceManagementService.updateDeviceSslCertification(
        TEST_ORGANISATION, TEST_DEVICE, TEST_UID, certification, TEST_MESSAGE_TYPE, TEST_PRIORITY);

    verify(this.osgpCoreRequestManager)
        .send(
            this.argumentRequestMessage.capture(),
            this.argumentMessageType.capture(),
            this.argumentPriority.capture(),
            this.argumentIpAddress.capture());

    final RequestMessage expectedRequestMessage =
        this.createNewRequestMessage(
            this.domainCoreMapper.map(certification, CertificationDto.class));

    assertThat(this.argumentRequestMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedRequestMessage);
    assertThat(this.argumentMessageType.getValue()).isEqualTo(TEST_MESSAGE_TYPE);
    assertThat(this.argumentPriority.getValue()).isEqualTo(TEST_PRIORITY);
    assertThat(this.argumentIpAddress.getValue()).isEqualTo(TEST_IP);
  }

  @Test
  public void testSetDeviceVerificationKeyIsNull() throws FunctionalException {
    this.deviceManagementService.setDeviceVerificationKey(
        TEST_ORGANISATION, TEST_DEVICE, TEST_UID, null, TEST_MESSAGE_TYPE, TEST_PRIORITY);

    // This method is not called since it comes after the check of the
    // verification
    verifyNoInteractions(this.osgpCoreRequestManager);
  }

  @Test
  public void testSetDeviceVerificationKey() throws FunctionalException {
    final Device device = mock(Device.class);
    when(device.getIpAddress()).thenReturn(TEST_IP);
    when(this.deviceDomainService.searchActiveDevice(TEST_DEVICE, ComponentType.DOMAIN_CORE))
        .thenReturn(device);
    this.deviceManagementService.setDeviceVerificationKey(
        TEST_ORGANISATION, TEST_DEVICE, TEST_UID, "testKey", TEST_MESSAGE_TYPE, TEST_PRIORITY);

    verify(this.osgpCoreRequestManager)
        .send(
            this.argumentRequestMessage.capture(),
            this.argumentMessageType.capture(),
            this.argumentPriority.capture(),
            this.argumentIpAddress.capture());

    final RequestMessage expectedRequestMessage = this.createNewRequestMessage("testKey");

    assertThat(this.argumentRequestMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedRequestMessage);
    assertThat(this.argumentMessageType.getValue()).isEqualTo(TEST_MESSAGE_TYPE);
    assertThat(this.argumentPriority.getValue()).isEqualTo(TEST_PRIORITY);
    assertThat(this.argumentIpAddress.getValue()).isEqualTo(TEST_IP);
  }

  @Test
  public void testSetDeviceLifeCycleStatus() throws FunctionalException {
    this.deviceManagementService.setDeviceLifecycleStatus(
        TEST_ORGANISATION, TEST_DEVICE, TEST_UID, DeviceLifecycleStatus.UNDER_TEST);

    final ArgumentCaptor<DeviceLifecycleStatus> argumentDeviceLifecycleStatus =
        ArgumentCaptor.forClass(DeviceLifecycleStatus.class);

    verify(this.transactionalDeviceService)
        .updateDeviceLifecycleStatus(
            this.argumentDeviceIdentification.capture(), argumentDeviceLifecycleStatus.capture());
    verify(this.webServiceResponseMessageSender).send(this.argumentResponseMessage.capture());

    final ResponseMessage expectedResponseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(TEST_UID)
            .withOrganisationIdentification(TEST_ORGANISATION)
            .withDeviceIdentification(TEST_DEVICE)
            .withMessageType(MessageType.SET_DEVICE_LIFECYCLE_STATUS.name())
            .withResult(ResponseMessageResultType.OK)
            .build();

    assertThat(this.argumentDeviceIdentification.getValue()).isEqualTo(TEST_DEVICE);
    assertThat(argumentDeviceLifecycleStatus.getValue())
        .isEqualTo(DeviceLifecycleStatus.UNDER_TEST);
    assertThat(this.argumentResponseMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedResponseMessage);
  }

  @Test
  public void testUpdateDeviceCdmaSettings() throws FunctionalException {
    final CdmaSettings cdmaSettings = new CdmaSettings("testSettings", (short) 1);
    this.deviceManagementService.updateDeviceCdmaSettings(
        TEST_ORGANISATION, TEST_DEVICE, TEST_UID, cdmaSettings);

    final ArgumentCaptor<CdmaSettings> argumentCdmaSettings =
        ArgumentCaptor.forClass(CdmaSettings.class);

    verify(this.transactionalDeviceService)
        .updateDeviceCdmaSettings(
            this.argumentDeviceIdentification.capture(), argumentCdmaSettings.capture());
    verify(this.webServiceResponseMessageSender).send(this.argumentResponseMessage.capture());

    final ResponseMessage expectedResponseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(TEST_UID)
            .withOrganisationIdentification(TEST_ORGANISATION)
            .withDeviceIdentification(TEST_DEVICE)
            .withMessageType(MessageType.UPDATE_DEVICE_CDMA_SETTINGS.name())
            .withResult(ResponseMessageResultType.OK)
            .build();

    assertThat(this.argumentDeviceIdentification.getValue()).isEqualTo(TEST_DEVICE);
    assertThat(argumentCdmaSettings.getValue()).isEqualTo(cdmaSettings);
    assertThat(this.argumentResponseMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedResponseMessage);
  }

  private RequestMessage createNewRequestMessage(final Serializable request) {
    return new RequestMessage(TEST_UID, TEST_ORGANISATION, TEST_DEVICE, request);
  }
}
