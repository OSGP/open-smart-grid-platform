package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.application.mapping.DomainCoreMapper;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.Certification;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.EventNotificationType;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeviceManagementServiceTest {
	
	@Mock
	private TransactionalDeviceService transactionalDeviceService;
	
	@Mock
	private OrganisationDomainService organisationDomainService;
	
	@Mock
	private DeviceDomainService deviceDomainService;
	
	@Mock
	private DomainCoreMapper domainCoreMapper;
	
	@Mock
	private OsgpCoreRequestMessageSender osgpCoreRequestManager;
	
	@Mock
	private WebServiceResponseMessageSender webServiceResponseMessageSender;
	
	@InjectMocks
	private DeviceManagementService deviceManagementService;
	
	@Test
	public void testSetEventNotifications() throws FunctionalException, UnknownEntityException {
		List<EventNotificationType> eventNotifications = new ArrayList<>();
		when(deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(new Device());
		
		this.deviceManagementService.setEventNotifications("testOrganisation", "testDevice", "testUid", eventNotifications, "testMessageType", 1);
		
		//Check if all methods are called with the proper parameters
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(deviceDomainService, times(1)).searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE);
		verify(domainCoreMapper, times(1)).mapAsList(eventNotifications, org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto.class);
		verify(osgpCoreRequestManager, times(1)).send(any(RequestMessage.class), eq("testMessageType"), eq(1), eq(null));
	}
	
	@Test
	public void testUpdateDeviceSslCertificationIsNull() throws FunctionalException, UnknownEntityException {
		this.deviceManagementService.updateDeviceSslCertification("testOrganisation", "testDevice", "testUid", null, "testMessageType", 1);
		
		//These methods are called before checking if the certificate is null
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(deviceDomainService, times(1)).searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE);
		
		//These methods are not called since they come after the check of the certificate
		verify(domainCoreMapper, never()).map(null,  org.opensmartgridplatform.dto.valueobjects.CertificationDto.class);
	}
	
	@Test
	public void testUpdateDeviceSslCertification() throws FunctionalException, UnknownEntityException {
		when(deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(new Device());
		Certification certification = new Certification("testUrl", "testDomain");
		
		this.deviceManagementService.updateDeviceSslCertification("testOrganisation", "testDevice", "testUid", certification, "testMessageType", 1);
		
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(deviceDomainService, times(1)).searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE);
		
		//These methods should now also be called
		verify(domainCoreMapper, times(1)).map(certification, org.opensmartgridplatform.dto.valueobjects.CertificationDto.class);
		verify(osgpCoreRequestManager, times(1)).send(any(RequestMessage.class), eq("testMessageType"), eq(1), eq(null));
	}
	
	@Test
	public void testSetDeviceVerificationKeyIsNull() throws FunctionalException, UnknownEntityException {
		this.deviceManagementService.setDeviceVerificationKey("testOrganisation", "testDevice", "testUid", null, "testMessageType", 1);
		
		//These methods are called before checking if the verification is null
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(deviceDomainService, times(1)).searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE);
		
		//These methods are not called since they come after the check of the verification
		verify(osgpCoreRequestManager, never()).send(any(RequestMessage.class), eq("testMessageType"), eq(1), eq(null));
	}
	
	@Test
	public void testSetDeviceVerificationKey() throws FunctionalException, UnknownEntityException {
		when(deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(new Device());
		this.deviceManagementService.setDeviceVerificationKey("testOrganisation", "testDevice", "testUid", "testKey", "testMessageType", 1);
		
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(deviceDomainService, times(1)).searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE);
		
		verify(osgpCoreRequestManager, times(1)).send(any(RequestMessage.class), eq("testMessageType"), eq(1), eq(null));
	}
	
	@Test
	public void testSetDeviceLifeCycleStatus() throws FunctionalException, UnknownEntityException {
		this.deviceManagementService.setDeviceLifecycleStatus("testOrganisation", "testDevice", "testUid", DeviceLifecycleStatus.UNDER_TEST);
		
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(transactionalDeviceService, times(1)).updateDeviceLifecycleStatus("testDevice", DeviceLifecycleStatus.UNDER_TEST);
		
		verify(webServiceResponseMessageSender, times(1)).send(any(ResponseMessage.class));
	}
	
	@Test
	public void testUpdateDeviceCdmaSettings() throws FunctionalException, UnknownEntityException {
		CdmaSettings cdmaSettings =  new CdmaSettings("testSettings", (short)1);
		this.deviceManagementService.updateDeviceCdmaSettings("testOrganisation", "testDevice", "testUid", cdmaSettings);
		
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(transactionalDeviceService, times(1)).updateDeviceCdmaSettings("testDevice", cdmaSettings);
		
		verify(webServiceResponseMessageSender, times(1)).send(any(ResponseMessage.class));
	}
}