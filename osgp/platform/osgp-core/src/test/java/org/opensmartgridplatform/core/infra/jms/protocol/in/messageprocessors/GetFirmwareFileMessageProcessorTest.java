package org.opensmartgridplatform.core.infra.jms.protocol.in.messageprocessors;

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

import org.opensmartgridplatform.core.infra.jms.protocol.in.ProtocolResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.dto.valueobjects.FirmwareFileDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class GetFirmwareFileMessageProcessorTest {

    @Mock
    private ProtocolResponseMessageSender protocolResponseMessageSender;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private FirmwareFileRepository firmwareFileRepository;

    @Mock
    private Device deviceMock;

    @Mock
    private FirmwareFile firmwareFileMock;

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

        final String firmwareFileIdentification = "fw";
        final byte[] firmwareFileBytes = firmwareFileIdentification.getBytes();

        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, firmwareFileIdentification);
        final ObjectMessage message = new ObjectMessageBuilder().withCorrelationUid(correlationUid)
                .withMessageType(DeviceFunction.GET_FIRMWARE_FILE.name()).withDeviceIdentification(deviceIdentification)
                .withObject(requestMessage).build();

        when(this.deviceMock.getDeviceIdentification()).thenReturn(deviceIdentification);
        when(this.deviceRepository.findByDeviceIdentification(deviceIdentification)).thenReturn(this.deviceMock);

        when(this.firmwareFileMock.getFilename()).thenReturn(firmwareFileIdentification);
        when(this.firmwareFileMock.getFile()).thenReturn(firmwareFileBytes);
        when(this.firmwareFileRepository.findByIdentification(firmwareFileIdentification)).thenReturn(this.firmwareFileMock);

        final byte[] expectedFile = firmwareFileBytes;
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
