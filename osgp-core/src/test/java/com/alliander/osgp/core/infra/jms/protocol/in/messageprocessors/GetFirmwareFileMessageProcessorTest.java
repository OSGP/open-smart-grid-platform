package com.alliander.osgp.core.infra.jms.protocol.in.messageprocessors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolResponseMessageSender;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.FirmwareFileDto;
import com.alliander.osgp.shared.infra.jms.MessageMetadata;
import com.alliander.osgp.shared.infra.jms.ObjectMessageBuilder;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class GetFirmwareFileMessageProcessorTest {

    @Mock
    private ProtocolResponseMessageSender protocolResponseMessageSender;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private FirmwareRepository firmwareRepository;

    @Mock
    private Device deviceMock;

    @Mock
    private Firmware firmwareMock;

    @InjectMocks
    private GetFirmwareFileMessageProcessor getFirmwareFileMessageProcessor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processMessageShouldSendFirmwareFile() throws JMSException {
        // arrange
        final String correlationUid = "corr-uid-1";
        final String organisationIdentification = "test-org";
        final String deviceIdentification = "dvc-1";

        final String firmwareIdentification = "fw";
        final byte[] firmwareFile = firmwareIdentification.getBytes();

        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, firmwareIdentification);
        final ObjectMessage message = new ObjectMessageBuilder().withCorrelationUid(correlationUid)
                .withMessageType(DeviceFunction.GET_FIRMWARE_FILE.name()).withDeviceIdentification(deviceIdentification)
                .withObject(requestMessage).build();

        when(this.deviceMock.getDeviceIdentification()).thenReturn(deviceIdentification);
        when(this.deviceRepository.findByDeviceIdentification(deviceIdentification)).thenReturn(this.deviceMock);

        when(this.firmwareMock.getFilename()).thenReturn(firmwareIdentification);
        when(this.firmwareMock.getFile()).thenReturn(firmwareFile);
        when(this.firmwareRepository.findByIdentification(firmwareIdentification)).thenReturn(this.firmwareMock);

        final byte[] expectedFile = firmwareFile;
        final String expectedMessageType = DeviceFunction.GET_FIRMWARE_FILE.name();

        final ArgumentCaptor<ProtocolResponseMessage> responseMessageArgumentCaptor = ArgumentCaptor
                .forClass(ProtocolResponseMessage.class);
        final ArgumentCaptor<String> messageTypeCaptor = ArgumentCaptor.forClass(String.class);

        // act
        this.getFirmwareFileMessageProcessor.processMessage(message);

        // assert
        verify(this.protocolResponseMessageSender, times(1)).send(responseMessageArgumentCaptor.capture(),
                messageTypeCaptor.capture(), any(ProtocolInfo.class), any(MessageMetadata.class));

        final byte[] actualFile = ((FirmwareFileDto) responseMessageArgumentCaptor.getValue().getDataObject())
                .getFirmwareFile();
        final String actualMessageType = messageTypeCaptor.getValue();

        assertArrayEquals(expectedFile, actualFile);
        assertEquals(expectedMessageType, actualMessageType);

    }

}
