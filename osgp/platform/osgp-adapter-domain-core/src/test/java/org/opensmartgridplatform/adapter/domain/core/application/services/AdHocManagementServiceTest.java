package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AdHocManagementServiceTest{

    @Mock
    private DeviceDomainService deviceDomainService;
    
    @Mock
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Mock
    private Device device;

    
    @InjectMocks
    private AdHocManagementService adHocManagementService;
    
    @Test
    public void testSetReboot() throws FunctionalException{
		when(this.device.getIpAddress()).thenReturn("127.0.0.1");
		when(this.deviceDomainService.searchActiveDevice(any(String.class), any(ComponentType.class))).thenReturn(
                this.device);

		final int priority = 1;
		final String messageType = "testType";
		final String correlationUuid = "correlationUid";
		final String deviceIdentification = "deviceIdentification";
		final String organisationIdentification = "orgIdentification";


        final ArgumentCaptor<RequestMessage> messageCaptor = ArgumentCaptor.forClass(RequestMessage.class);
        final ArgumentCaptor<String> messageTypeCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(int.class);
        final ArgumentCaptor<String> ipCaptor = ArgumentCaptor.forClass(String.class);

        this.adHocManagementService.setReboot(organisationIdentification, deviceIdentification, correlationUuid, messageType, priority);
        verify(this.osgpCoreRequestMessageSender).send(messageCaptor.capture(),messageTypeCaptor.capture(),intCaptor.capture(),ipCaptor.capture());

        final RequestMessage message = messageCaptor.getValue();
        assertEquals(correlationUuid,message.getCorrelationUid());
        assertEquals(organisationIdentification,message.getOrganisationIdentification());
        assertEquals(priority,intCaptor.getValue());
        assertEquals("127.0.0.1",ipCaptor.getValue());
        assertEquals(messageType, messageTypeCaptor.getValue());
    }
}
