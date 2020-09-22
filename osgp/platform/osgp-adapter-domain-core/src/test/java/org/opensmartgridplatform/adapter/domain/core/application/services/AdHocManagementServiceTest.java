package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AdHocManagementServiceTest{

    @Mock
    private DeviceDomainService deviceDomainService;
    
    @Mock
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;
    
    @Mock
    private OrganisationDomainService organisationDomainService;
    
    @InjectMocks
    private AdHocManagementService adHocManagementService;
    
    @Test
    public void testSetReboot() throws FunctionalException{
		when(deviceDomainService.searchActiveDevice(any(String.class), any(ComponentType.class))).thenReturn(new Device());
		adHocManagementService.setReboot("organisationIdentification", "deviceIdentification", "correlationUid", "messageType", 1);
		verify(osgpCoreRequestMessageSender, times(1)).send(any(RequestMessage.class), eq("messageType"), eq(1), eq(null));
    }
}