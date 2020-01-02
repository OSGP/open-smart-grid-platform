/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.FirmwareService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RetryHeaderFactory;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UpdateFirmwareRequestMessageProcessorTest {
    @Mock
    protected DlmsConnectionHelper connectionHelper;

    @Mock
    protected DeviceResponseMessageSender responseMessageSender;

    @Mock
    private RetryHeaderFactory retryHeaderFactory;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private FirmwareService firmwareService;

    @Mock
    private OsgpRequestMessageSender osgpRequestMessageSender;

    @Mock
    private DomainHelperService domainHelperService;

    @Mock
    private DlmsConnectionManager dlmsConnectionManagerMock;

    @Mock
    private DlmsMessageListener messageListenerMock;

    private DlmsDevice device;

    @InjectMocks
    private UpdateFirmwareRequestMessageProcessor processor;

    @BeforeEach
    public void setup() throws OsgpException {
        MockitoAnnotations.initMocks(this);

        this.device = new DlmsDeviceBuilder().withHls5Active(true).build();
        when(this.domainHelperService.findDlmsDevice(any(MessageMetadata.class))).thenReturn(this.device);
        when(this.dlmsConnectionManagerMock.getDlmsMessageListener()).thenReturn(this.messageListenerMock);
        when(this.connectionHelper.createConnectionForDevice(same(this.device), any(DlmsMessageListener.class)))
                .thenReturn(this.dlmsConnectionManagerMock);
    }

    @Test
    public void processMessageShouldSendFirmwareFileRequestWhenFirmwareFileNotAvailable() throws JMSException {
        // Arrange
        final String firmwareIdentification = "unavailable";
        final ObjectMessage message = new ObjectMessageBuilder().withObject(firmwareIdentification).build();
        when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(false);

        // Act
        this.processor.processMessage(message);

        // Assert
        verify(this.osgpRequestMessageSender, times(1)).send(any(RequestMessage.class), any(String.class),
                any(MessageMetadata.class));
    }

    @Test
    public void processMessageShouldNotSendFirmwareFileRequestWhenFirmwareFileAvailable() throws JMSException {
        // Arrange
        final String firmwareIdentification = "unavailable";
        final ObjectMessage message = new ObjectMessageBuilder().withObject(firmwareIdentification).build();
        when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(true);

        // Act
        this.processor.processMessage(message);

        // Assert
        verify(this.osgpRequestMessageSender, never()).send(any(RequestMessage.class), any(String.class),
                any(MessageMetadata.class));
    }

    @Test
    public void processMessageShouldUpdateFirmwareWhenFirmwareFileAvailable() throws JMSException, OsgpException {
        // Arrange
        final String firmwareIdentification = "available";
        final ObjectMessage message = new ObjectMessageBuilder().withObject(firmwareIdentification).build();
        when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(true);

        // Act
        this.processor.processMessage(message);

        // Assert
        verify(this.configurationService, times(1)).updateFirmware(null, this.device, firmwareIdentification);
    }

    @Test
    public void processMessageShouldNotUpdateFirmwareWhenFirmwareFileNotAvailable() throws JMSException, OsgpException {
        // Arrange
        final String firmwareIdentification = "unavailable";
        final ObjectMessage message = new ObjectMessageBuilder().withObject(firmwareIdentification).build();
        when(this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)).thenReturn(false);

        // Act
        this.processor.processMessage(message);

        // Assert
        verify(this.firmwareService, times(0)).updateFirmware(this.dlmsConnectionManagerMock, this.device,
                firmwareIdentification);
    }
}
