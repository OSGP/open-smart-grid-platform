/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core.processors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.FirmwareService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.OsgpExceptionConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RetryHeaderFactory;
import org.opensmartgridplatform.dto.valueobjects.FirmwareFileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

public class GetFirmwareFileResponseMessageProcessorTest {

    @Mock
    protected DeviceResponseMessageSender responseMessageSender;

    @Mock
    protected DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender;

    @Mock
    protected OsgpExceptionConverter osgpExceptionConverter;

    @Mock
    protected DomainHelperService domainHelperService;

    @Mock
    protected DlmsConnectionFactory dlmsConnectionFactory;

    @Mock
    protected DlmsMessageListener dlmsMessageListenerMock;

    @Mock
    private RetryHeaderFactory retryHeaderFactory;

    @Mock
    private FirmwareService firmwareService;

    @Mock
    private DlmsConnectionHolder dlmsConnectionHolderMock;

    @Mock
    private DlmsDevice dlmsDeviceMock;

    @InjectMocks
    private GetFirmwareFileResponseMessageProcessor getFirmwareFileResponseMessageProcessor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processMessageShouldSendOkResponseMessageContainingFirmwareVersions()
            throws OsgpException, JMSException {
        // arrange
        final FirmwareFileDto firmwareFileDto = this.setupFirmwareFileDto();
        final ResponseMessage responseMessage = this.setupResponseMessage(firmwareFileDto);
        final ObjectMessage message = new ObjectMessageBuilder().withMessageType(MessageType.GET_FIRMWARE_FILE.name())
                .withObject(responseMessage).build();
        final UpdateFirmwareResponseDto updateFirmwareResponseDto = new UpdateFirmwareResponseDto(
                firmwareFileDto.getFirmwareIdentification(), new LinkedList<>());

        final ArgumentCaptor<ResponseMessage> responseMessageArgumentCaptor = ArgumentCaptor
                .forClass(ResponseMessage.class);

        when(this.domainHelperService.findDlmsDevice(any(MessageMetadata.class))).thenReturn(this.dlmsDeviceMock);
        when(this.dlmsConnectionFactory.getConnection(this.dlmsDeviceMock, null))
                .thenReturn(this.dlmsConnectionHolderMock);
        when(this.dlmsConnectionHolderMock.getDlmsMessageListener()).thenReturn(this.dlmsMessageListenerMock);
        when(this.dlmsDeviceMock.isInDebugMode()).thenReturn(false);
        when(this.firmwareService.updateFirmware(this.dlmsConnectionHolderMock, this.dlmsDeviceMock, firmwareFileDto))
                .thenReturn(updateFirmwareResponseDto);

        // act
        this.getFirmwareFileResponseMessageProcessor.processMessage(message);

        // assert
        verify(this.responseMessageSender, times(1)).send(responseMessageArgumentCaptor.capture());

        assertThat(responseMessageArgumentCaptor.getValue().getDataObject(), is(updateFirmwareResponseDto));
        assertThat(responseMessageArgumentCaptor.getValue().getResult(), is(ResponseMessageResultType.OK));
    }

    // @Test
    public void handleMessageShouldCallUpdateFirmware()
            throws OsgpException {
        // arrange
        final FirmwareFileDto firmwareFileDto = this.setupFirmwareFileDto();
        final ResponseMessage responseMessage = this.setupResponseMessage(firmwareFileDto);

        // act
        this.getFirmwareFileResponseMessageProcessor.handleMessage(this.dlmsConnectionHolderMock, this.dlmsDeviceMock,
                responseMessage);

        // assert
        verify(this.firmwareService, times(1)).updateFirmware(this.dlmsConnectionHolderMock, this.dlmsDeviceMock,
                firmwareFileDto);
    }

    private FirmwareFileDto setupFirmwareFileDto() {
        return new FirmwareFileDto("fw", "fw".getBytes());
    }

    private ResponseMessage setupResponseMessage(final FirmwareFileDto firmwareFileDto) {
        return ResponseMessage.newResponseMessageBuilder().withCorrelationUid("corr-uid-1")
                .withOrganisationIdentification("test-org").withDeviceIdentification("dvc-01")
                .withResult(ResponseMessageResultType.OK).withDataObject(firmwareFileDto).build();
    }
}
