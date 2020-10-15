package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
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

    @Mock
    private OrganisationDomainService organisationDomainService;

    
    @InjectMocks
    private AdHocManagementService adHocManagementService;
    
    @Test
    public void testSetReboot() throws FunctionalException, UnknownEntityException {
		when(this.device.getIpAddress()).thenReturn("127.0.0.1");
		when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(Mockito.mock(Organisation.class));
		when(this.deviceDomainService.searchActiveDevice(any(String.class), any(ComponentType.class))).thenReturn(this.device);

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

        assertThat(correlationUuid).isEqualTo(message.getCorrelationUid());
        assertThat(organisationIdentification).isEqualTo(message.getOrganisationIdentification());
        assertThat(priority).isEqualTo(intCaptor.getValue());
        assertThat("127.0.0.1").isEqualTo(ipCaptor.getValue());
        assertThat(messageType).isEqualTo(messageTypeCaptor.getValue());
    }
}
