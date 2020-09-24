package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.application.mapping.DomainCoreMapper;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.Certification;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.EventNotificationType;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationMessageDataContainerDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeviceManagementServiceTest {
	
	@Mock
	private TransactionalDeviceService transactionalDeviceService;
	
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

	@Captor
	private ArgumentCaptor<RequestMessage> argumentReqM;

	@Captor
	private ArgumentCaptor<ResponseMessage> argumentResM;

	@Captor
	private ArgumentCaptor<String> argumentStringOne;

	@Captor
	private ArgumentCaptor<String> argumentStringTwo;

	@Captor
	private ArgumentCaptor<Integer> argumentInt;
	
	@Test
	public void testSetEventNotifications() throws FunctionalException {
		final List<EventNotificationType> eventNotifications = Arrays.asList(
				EventNotificationType.COMM_EVENTS,
				EventNotificationType.DIAG_EVENTS);
		final Device device = mock(Device.class);
		when(device.getIpAddress()).thenReturn("testIp");
		when(this.deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(device);
		
		this.deviceManagementService.setEventNotifications("testOrganisation", "testDevice", "testUid",
				eventNotifications, "testMessageType", 1);

		verify(this.osgpCoreRequestManager).send(this.argumentReqM.capture(), this.argumentStringOne.capture(),
				this.argumentInt.capture(), this.argumentStringTwo.capture());

		final RequestMessage expectedRM = new RequestMessage("testUid", "testOrganisation", "testDevice",
				new EventNotificationMessageDataContainerDto(this.domainCoreMapper.mapAsList(eventNotifications,
						org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto.class)));

		assertThat(this.argumentReqM.getValue()).usingRecursiveComparison().isEqualTo(expectedRM);
		assertThat(this.argumentStringOne.getValue()).isEqualTo("testMessageType");
		assertThat(this.argumentInt.getValue()).isEqualTo(1);
		assertThat(this.argumentStringTwo.getValue()).isEqualTo("testIp");
	}
	
	@Test
	public void testUpdateDeviceSslCertificationIsNull() throws FunctionalException {
		this.deviceManagementService.updateDeviceSslCertification("testOrganisation", "testDevice", "testUid",
				null, "testMessageType", 1);

		//This method is not called since it comes after the check of the certificate
		verifyNoInteractions(this.domainCoreMapper);
	}
	
	@Test
	public void testUpdateDeviceSslCertification() throws FunctionalException {
		final Device device = mock(Device.class);
		when(device.getIpAddress()).thenReturn("testIp");
		when(this.deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(device);
		final Certification certification = new Certification("testUrl", "testDomain");
		
		this.deviceManagementService.updateDeviceSslCertification("testOrganisation", "testDevice", "testUid",
				certification, "testMessageType", 1);

		verify(this.osgpCoreRequestManager).send(this.argumentReqM.capture(), this.argumentStringOne.capture(),
				this.argumentInt.capture(), this.argumentStringTwo.capture());

		final RequestMessage expectedRM = new RequestMessage("testUid", "testOrganisation", "testDevice",
				this.domainCoreMapper.map(certification,
						org.opensmartgridplatform.dto.valueobjects.CertificationDto.class));

		assertThat(this.argumentReqM.getValue()).usingRecursiveComparison().isEqualTo(expectedRM);
		assertThat(this.argumentStringOne.getValue()).isEqualTo("testMessageType");
		assertThat(this.argumentInt.getValue()).isEqualTo(1);
		assertThat(this.argumentStringTwo.getValue()).isEqualTo("testIp");
	}
	
	@Test
	public void testSetDeviceVerificationKeyIsNull() throws FunctionalException {
		this.deviceManagementService.setDeviceVerificationKey("testOrganisation", "testDevice", "testUid",
				null, "testMessageType", 1);

		//This method is not called since it comes after the check of the verification
		verifyNoInteractions(this.osgpCoreRequestManager);
	}
	
	@Test
	public void testSetDeviceVerificationKey() throws FunctionalException {
		final Device device = mock(Device.class);
		when(device.getIpAddress()).thenReturn("testIp");
		when(this.deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(device);
		this.deviceManagementService.setDeviceVerificationKey("testOrganisation", "testDevice", "testUid",
				"testKey", "testMessageType", 1);

		verify(this.osgpCoreRequestManager).send(this.argumentReqM.capture(), this.argumentStringOne.capture(),
				this.argumentInt.capture(), this.argumentStringTwo.capture());

		final RequestMessage expectedRM = new RequestMessage("testUid", "testOrganisation", "testDevice", "testKey");

		assertThat(this.argumentReqM.getValue()).usingRecursiveComparison().isEqualTo(expectedRM);
		assertThat(this.argumentStringOne.getValue()).isEqualTo("testMessageType");
		assertThat(this.argumentInt.getValue()).isEqualTo(1);
		assertThat(this.argumentStringTwo.getValue()).isEqualTo("testIp");
	}
	
	@Test
	public void testSetDeviceLifeCycleStatus() throws FunctionalException {
		this.deviceManagementService.setDeviceLifecycleStatus("testOrganisation", "testDevice", "testUid", DeviceLifecycleStatus.UNDER_TEST);

		final ArgumentCaptor<DeviceLifecycleStatus> argumentDeviceLifecycleStatus =
				ArgumentCaptor.forClass(DeviceLifecycleStatus.class);

		verify(this.transactionalDeviceService).updateDeviceLifecycleStatus(this.argumentStringOne.capture(),
				argumentDeviceLifecycleStatus.capture());
		verify(this.webServiceResponseMessageSender).send(this.argumentResM.capture());

		final ResponseMessage expectedRM = ResponseMessage.newResponseMessageBuilder()
				.withCorrelationUid("testUid")
				.withOrganisationIdentification("testOrganisation")
				.withDeviceIdentification("testDevice")
				.withResult(ResponseMessageResultType.OK)
				.build();

		assertThat(this.argumentStringOne.getValue()).isEqualTo("testDevice");
		assertThat(argumentDeviceLifecycleStatus.getValue()).isEqualTo(DeviceLifecycleStatus.UNDER_TEST);
		assertThat(this.argumentResM.getValue()).usingRecursiveComparison().isEqualTo(expectedRM);
	}
	
	@Test
	public void testUpdateDeviceCdmaSettings() throws FunctionalException {
		final CdmaSettings cdmaSettings =  new CdmaSettings("testSettings", (short)1);
		this.deviceManagementService.updateDeviceCdmaSettings("testOrganisation", "testDevice", "testUid", cdmaSettings);

		final ArgumentCaptor<CdmaSettings> argumentCdmaSettings = ArgumentCaptor.forClass(CdmaSettings.class);

		verify(this.transactionalDeviceService).updateDeviceCdmaSettings(this.argumentStringOne.capture(),
				argumentCdmaSettings.capture());
		verify(this.webServiceResponseMessageSender).send(this.argumentResM.capture());

		final ResponseMessage expectedRM = ResponseMessage.newResponseMessageBuilder()
				.withCorrelationUid("testUid")
				.withOrganisationIdentification("testOrganisation")
				.withDeviceIdentification("testDevice")
				.withResult(ResponseMessageResultType.OK)
				.build();

		assertThat(this.argumentStringOne.getValue()).isEqualTo("testDevice");
		assertThat(argumentCdmaSettings.getValue()).isEqualTo(cdmaSettings);
		assertThat(this.argumentResM.getValue()).usingRecursiveComparison().isEqualTo(expectedRM);
	}
}