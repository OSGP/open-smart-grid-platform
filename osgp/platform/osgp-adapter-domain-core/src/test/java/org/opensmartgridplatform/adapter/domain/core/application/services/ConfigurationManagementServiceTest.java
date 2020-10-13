/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
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
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.Configuration;
import org.opensmartgridplatform.domain.core.valueobjects.RelayConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.RelayMap;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Qualifier;

@ExtendWith(MockitoExtension.class)
public class ConfigurationManagementServiceTest {
    @Mock
    private WebServiceResponseMessageSender webServiceResponseMessageSender;
    @Mock
    private SsldRepository ssldRepository;
    @Mock
    private ConfigurationDto configurationDto;
    @Mock
    private DomainCoreMapper domainCoreMapper;
    @Mock
    private OrganisationDomainService organisationDomainService;
    @Mock
    private DeviceDomainService deviceDomainService;
    @Mock
    private CorrelationIds correlationIds;
    @Mock
    private Configuration configuration;
    @Mock
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;
    @Mock
    private Ssld ssld;
    @Mock
    private Device device;
    @Captor
    private ArgumentCaptor<RequestMessage> requestMessageArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> messageTypeArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> ipAddressArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> messagePriorityArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> scheduledTimeArgumentCaptor;
    @Captor
    private ArgumentCaptor<ResponseMessage> responseMessageArgumentCaptor;
    @InjectMocks
    @Qualifier("organisationDomainService")
    private ConfigurationManagementService configurationManagementService;

    private static final String organisationIdentification = "orgIdentification";
    private static final String deviceIdentification = "deviceIdentification";
    private static final String correlationUid = "correlationUid";
    private static final long scheduleTime = 1;
    private static final String messageType = "none";
    private static final int messagePriority = 1;
    private static final String ipAddress = "333.333.1.22";

    private OsgpException exception;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        // make correlationIds
        this.correlationIds = new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
        this.configurationManagementService = new ConfigurationManagementService();

        // do injection using reflection
        // Inject Mocks doesn't inject these mocks, possibly because they are in the parent class of
        // ConfigurationManagementService
        this.injectionUsingReflection("organisationDomainService",
                this.configurationManagementService, this.organisationDomainService);
        this.injectionUsingReflection("deviceDomainService", this.configurationManagementService,
                this.deviceDomainService);
        this.injectionUsingReflection("domainCoreMapper", this.configurationManagementService,
                this.domainCoreMapper);
        this.injectionUsingReflection("osgpCoreRequestMessageSender",
                this.configurationManagementService, this.osgpCoreRequestMessageSender);
        this.injectionUsingReflection("webServiceResponseMessageSender",
                this.configurationManagementService, this.webServiceResponseMessageSender);
        this.injectionUsingReflection("ssldRepository", this.configurationManagementService,
                this.ssldRepository);

        System.setOut(new PrintStream(this.outContent));
    }

    @Test
    public void testTrySetConfiguration() throws FunctionalException, UnknownEntityException {
        final Organisation testOrganisation = new Organisation();

        final RelayMap relayMap = new RelayMap(1, 1, RelayType.LIGHT, "1");
        final List<RelayMap> relayMapList = Arrays.asList(relayMap);

        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(testOrganisation);
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(this.device);
        when(this.configuration.getRelayConfiguration()).thenReturn(new RelayConfiguration(relayMapList));
        when(this.ssldRepository.findById(any())).thenReturn(java.util.Optional.of(this.ssld));
        when(this.domainCoreMapper.map(any(), any())).thenReturn(this.configurationDto);
        doNothing().when(this.ssld).updateOutputSettings(any());
        when(this.device.getIpAddress()).thenReturn(ipAddress);

        this.configurationManagementService.setConfiguration(this.correlationIds, this.configuration, scheduleTime,
                messageType, messagePriority);

        verify(this.osgpCoreRequestMessageSender).sendWithScheduledTime(this.requestMessageArgumentCaptor.capture(),
                this.messageTypeArgumentCaptor.capture(), this.messagePriorityArgumentCaptor.capture(),
                this.ipAddressArgumentCaptor.capture(), this.scheduledTimeArgumentCaptor.capture());

        assertThat(this.requestMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("correlationUid");
        assertThat(this.requestMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("orgIdentification");
        assertThat(this.requestMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("deviceIdentification");
        assertThat(this.messageTypeArgumentCaptor.getValue()).isEqualTo(messageType);
        assertThat(this.messagePriorityArgumentCaptor.getValue()).isEqualTo(1);
        assertThat(this.ipAddressArgumentCaptor.getValue()).isEqualTo(ipAddress);
        assertThat(this.scheduledTimeArgumentCaptor.getValue()).isEqualTo(1);
    }

    @Test
    public void testTrySetConfigurationWithNoConfiguration() throws Exception {
        this.configurationManagementService.setConfiguration(this.correlationIds, null, scheduleTime, messageType, messagePriority);

        assertThat(this.outContent.toString()).contains("Configuration is empty, skip sending a request to device");
    }

    @Test
    public void testTrySetConfigurationWithNullRelayConfiguration() throws UnknownEntityException, FunctionalException {
        final Organisation testOrganisation = new Organisation();
        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(testOrganisation);
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(new Device());
        when(this.configuration.getRelayConfiguration()).thenReturn(null);

        this.configurationManagementService.setConfiguration(this.correlationIds, this.configuration, scheduleTime,
                messageType, messagePriority);

        verify(this.osgpCoreRequestMessageSender).sendWithScheduledTime(this.requestMessageArgumentCaptor.capture(),
                this.messageTypeArgumentCaptor.capture(), this.messagePriorityArgumentCaptor.capture(),
                this.ipAddressArgumentCaptor.capture(), this.scheduledTimeArgumentCaptor.capture());

        assertThat(this.requestMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("correlationUid");
        assertThat(this.requestMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("orgIdentification");
        assertThat(this.requestMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("deviceIdentification");
        assertThat(this.messageTypeArgumentCaptor.getValue()).isEqualTo(messageType);
        assertThat(this.messagePriorityArgumentCaptor.getValue()).isEqualTo(1);
        assertThat(this.ipAddressArgumentCaptor.getValue()).isEqualTo(null);
        assertThat(this.scheduledTimeArgumentCaptor.getValue()).isEqualTo(1);
    }

    @Test
    public void testGetConfiguration() throws UnknownEntityException, FunctionalException {
        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(new Organisation());
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(this.device);
        when(this.device.getIpAddress()).thenReturn(ipAddress);

        this.configurationManagementService.getConfiguration(organisationIdentification, deviceIdentification,
                correlationUid, messageType, messagePriority);

        verify(this.osgpCoreRequestMessageSender).send(this.requestMessageArgumentCaptor.capture(),
                this.messageTypeArgumentCaptor.capture(), this.messagePriorityArgumentCaptor.capture(),
                this.ipAddressArgumentCaptor.capture());

        assertThat(this.requestMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("correlationUid");
        assertThat(this.requestMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("orgIdentification");
        assertThat(this.requestMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("deviceIdentification");
        assertThat(this.messageTypeArgumentCaptor.getValue()).isEqualTo(messageType);
        assertThat(this.messagePriorityArgumentCaptor.getValue()).isEqualTo(1);
        assertThat(this.ipAddressArgumentCaptor.getValue()).isEqualTo(ipAddress);
    }

    @Test
    public void testHandleGetConfigurationResponse() {
        this.exception = null;

        when(this.ssldRepository.findByDeviceIdentification("deviceIdentification")).thenReturn(new Ssld());

        this.configurationManagementService.handleGetConfigurationResponse(this.configurationDto, this.correlationIds,
                messageType, messagePriority,
                ResponseMessageResultType.OK, this.exception);

        verify(this.webServiceResponseMessageSender).send(this.responseMessageArgumentCaptor.capture());

        assertThat(this.responseMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("correlationUid");
        assertThat(this.responseMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("orgIdentification");
        assertThat(this.responseMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("deviceIdentification");
        assertThat(this.responseMessageArgumentCaptor.getValue().getResult()).isEqualTo(ResponseMessageResultType.OK);
    }

    @Test
    public void testHandleGetConfigurationResponseWithException() {
        this.exception = new OsgpException(ComponentType.DOMAIN_ADMIN, "orgIdentification");

        this.configurationManagementService.handleGetConfigurationResponse(this.configurationDto, this.correlationIds,
                messageType, messagePriority, ResponseMessageResultType.OK, this.exception);

        assertThat(this.outContent.toString()).contains("Unexpected Exception for messageType:");

        verify(this.webServiceResponseMessageSender).send(this.responseMessageArgumentCaptor.capture());

        assertThat(this.responseMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("correlationUid");
        assertThat(this.responseMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("orgIdentification");
        assertThat(this.responseMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("deviceIdentification");
        assertThat(this.responseMessageArgumentCaptor.getValue().getResult()).isEqualTo(ResponseMessageResultType.NOT_OK);
    }

    @Test
    public void testswitchConfiguration() throws UnknownEntityException, FunctionalException {
        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(new Organisation());
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(this.device);
        when(this.device.getIpAddress()).thenReturn(ipAddress);

        this.configurationManagementService.switchConfiguration(organisationIdentification,
                deviceIdentification, correlationUid, messageType, messagePriority, "orgIdentification");

        verify(this.osgpCoreRequestMessageSender).send(this.requestMessageArgumentCaptor.capture(),
                this.messageTypeArgumentCaptor.capture(), this.messagePriorityArgumentCaptor.capture(),
                this.ipAddressArgumentCaptor.capture());

        assertThat(this.requestMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("correlationUid");
        assertThat(this.requestMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("orgIdentification");
        assertThat(this.requestMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("deviceIdentification");
        assertThat(this.messageTypeArgumentCaptor.getValue()).isEqualTo(messageType);
        assertThat(this.messagePriorityArgumentCaptor.getValue()).isEqualTo(1);
        assertThat(this.ipAddressArgumentCaptor.getValue()).isEqualTo(ipAddress);
    }

    private void injectionUsingReflection(final String fieldName, final Object instance, final Object newValue) throws
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final Field field = AbstractService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, newValue);
    }
}
