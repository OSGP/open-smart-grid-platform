package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.mockito.Mockito.when;

import java.util.Arrays;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.application.mapping.DomainCoreMapper;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.adapter.domain.shared.GetStatusResponse;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeviceInstallationServiceTest {
	
	@Mock
	private OrganisationDomainService organisationDomainService;
	
	@Mock
	private DeviceDomainService deviceDomainService;
	
	@Mock
	private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;
	
	@Mock
	private WebServiceResponseMessageSender webServiceResponseMessageSender;
	
	@Mock
	private DomainCoreMapper domainCoreMapper;
	
	@Mock
	private SsldRepository ssldRepository;
	
	@InjectMocks
	private DeviceInstallationService deviceInstallationService;
	
	@Test
	public void testGetStatusTestDeviceTypeIsNotLMDType() throws FunctionalException, UnknownEntityException {
		when(deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(new Device());
		
		this.deviceInstallationService.getStatus("testOrganisation", "testDevice", "testUid", "testMessageType", 1);
		
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(deviceDomainService, times(1)).searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE);
		
		verify(osgpCoreRequestMessageSender, times(1)).send(any(RequestMessage.class), eq("testMessageType"), eq(1), eq(null));
	}
	
	@Test
	public void testGetStatusTestDeviceTypeIsLMDType() throws FunctionalException, UnknownEntityException {
		Device mockedDevice = Mockito.mock(Device.class);
		when(mockedDevice.getDeviceType()).thenReturn("LMD");
		when(deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(mockedDevice);
		
		this.deviceInstallationService.getStatus("testOrganisation", "testDevice", "testUid", "testMessageType", 1);
		
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(deviceDomainService, times(1)).searchActiveDevice("testDevice",  ComponentType.DOMAIN_CORE);
		
		verify(osgpCoreRequestMessageSender, times(1)).send(any(RequestMessage.class), eq("GET_LIGHT_SENSOR_STATUS"), eq(1), eq(null));
	}
	
	@Test
	public void testHandleGetStatusResponseNotOk() throws FunctionalException, UnknownEntityException {
		ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
		
		this.deviceInstallationService.handleGetStatusResponse(null, new CorrelationIds("testOrganisation", "testDevice", "testUid"), "testMessageType", 1, ResponseMessageResultType.NOT_OK, null);
		
		verify(domainCoreMapper, never()).map(null, DeviceStatus.class);
		verify(webServiceResponseMessageSender).send(argument.capture());
		
		assertEquals("testOrganisation", argument.getValue().getOrganisationIdentification());
		assertEquals("testDevice", argument.getValue().getDeviceIdentification());
		assertEquals("testUid", argument.getValue().getCorrelationUid());
		assertEquals(null, argument.getValue().getOsgpException());
		assertEquals(1, argument.getValue().getMessagePriority());
		assertEquals(ResponseMessageResultType.NOT_OK, argument.getValue().getResult());
	}
	
	@Test
	public void testHandleGetStatusResponseOkLMDStatusNotNull() throws FunctionalException, UnknownEntityException {
		ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
		
		when(domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(new DeviceStatus(null, null, null, null, 0));
		
		Device mockedDevice = Mockito.mock(Device.class);
		when(mockedDevice.getDeviceType()).thenReturn(LightMeasurementDevice.LMD_TYPE);
		when(deviceDomainService.searchDevice("testDevice")).thenReturn(mockedDevice);
		
		this.deviceInstallationService.handleGetStatusResponse(null, new CorrelationIds("testOrganisation", "testDevice", "testUid"), "testMessageType", 1, ResponseMessageResultType.OK, null);
		
		verify(webServiceResponseMessageSender).send(argument.capture());
		assertEquals("testOrganisation", argument.getValue().getOrganisationIdentification());
		assertEquals("testDevice", argument.getValue().getDeviceIdentification());
		assertEquals("testUid", argument.getValue().getCorrelationUid());
		assertEquals(null, argument.getValue().getOsgpException());
		assertEquals(1, argument.getValue().getMessagePriority());
		assertEquals(ResponseMessageResultType.OK, argument.getValue().getResult());
	}
	
	@Test
	public void testHandleGetStatusResponseOkLMDStatusNull() throws FunctionalException, UnknownEntityException {
		ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
		
		when(domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(null);
		
		Device mockedDevice = Mockito.mock(Device.class);
		when(mockedDevice.getDeviceType()).thenReturn(LightMeasurementDevice.LMD_TYPE);
		when(deviceDomainService.searchDevice("testDevice")).thenReturn(mockedDevice);
		
		this.deviceInstallationService.handleGetStatusResponse(null, new CorrelationIds("testOrganisation", "testDevice", "testUid"), "testMessageType", 1, ResponseMessageResultType.OK, null);
		
		verify(webServiceResponseMessageSender).send(argument.capture());
		assertEquals("testOrganisation", argument.getValue().getOrganisationIdentification());
		assertEquals("testDevice", argument.getValue().getDeviceIdentification());
		assertEquals("testUid", argument.getValue().getCorrelationUid());
		assertEquals(ComponentType.DOMAIN_CORE, argument.getValue().getOsgpException().getComponentType());
		assertEquals("Light measurement device was not able to report light sensor status", argument.getValue().getOsgpException().getMessage());
		assertEquals(1, argument.getValue().getMessagePriority());
		assertEquals(ResponseMessageResultType.NOT_OK, argument.getValue().getResult());
	}
	
	@Test
	public void testHandleGetStatusResponseOkNotLMDStatusNotNull() throws FunctionalException, UnknownEntityException {
		ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
		
		DeviceStatus mockedStatus = Mockito.mock(DeviceStatus.class);
		when(mockedStatus.getLightValues()).thenReturn(Arrays.asList());
		when(domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(mockedStatus);
		
		Device mockedDevice = Mockito.mock(Device.class);
		when(mockedDevice.getDeviceType()).thenReturn(null);
		when(deviceDomainService.searchDevice("testDevice")).thenReturn(mockedDevice);
		
		when(ssldRepository.findByDeviceIdentification("testDevice")).thenReturn(new Ssld());
		
		this.deviceInstallationService.handleGetStatusResponse(null, new CorrelationIds("testOrganisation", "testDevice", "testUid"), "testMessageType", 1, ResponseMessageResultType.OK, null);
		
		verify(webServiceResponseMessageSender).send(argument.capture());
		assertEquals("testOrganisation", argument.getValue().getOrganisationIdentification());
		assertEquals("testDevice", argument.getValue().getDeviceIdentification());
		assertEquals("testUid", argument.getValue().getCorrelationUid());
		assertEquals(1, argument.getValue().getMessagePriority());
		assertEquals(ResponseMessageResultType.OK, argument.getValue().getResult());
	}
	
	@Test
	public void testHandleGetStatusResponseOkNotLMDStatusNull() throws FunctionalException, UnknownEntityException {
		ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
		
		when(domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(null);
		
		Device mockedDevice = Mockito.mock(Device.class);
		when(mockedDevice.getDeviceType()).thenReturn(null);
		when(deviceDomainService.searchDevice("testDevice")).thenReturn(mockedDevice);
		
		when(ssldRepository.findByDeviceIdentification("testDevice")).thenReturn(new Ssld());
		
		this.deviceInstallationService.handleGetStatusResponse(null, new CorrelationIds("testOrganisation", "testDevice", "testUid"), "testMessageType", 1, ResponseMessageResultType.OK, null);
		
		verify(webServiceResponseMessageSender).send(argument.capture());
		assertEquals("testOrganisation", argument.getValue().getOrganisationIdentification());
		assertEquals("testDevice", argument.getValue().getDeviceIdentification());
		assertEquals("testUid", argument.getValue().getCorrelationUid());
		assertEquals(ComponentType.DOMAIN_CORE, argument.getValue().getOsgpException().getComponentType());
		assertEquals("SSLD was not able to report relay status", argument.getValue().getOsgpException().getMessage());
		assertEquals(1, argument.getValue().getMessagePriority());
		assertEquals(ResponseMessageResultType.NOT_OK, argument.getValue().getResult());
	}
	
	@Test
	public void testStartSelfTest() throws FunctionalException, UnknownEntityException {
		when(deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(new Device());
		
		this.deviceInstallationService.startSelfTest("testDevice", "testOrganisation", "testUid", "testMessageType", 1);
		
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(deviceDomainService, times(1)).searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE);
	}
	
	@Test
	public void testStopSelfTest() throws FunctionalException, UnknownEntityException {
		when(deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(new Device());
		
		this.deviceInstallationService.stopSelfTest("testDevice", "testOrganisation", "testUid", "testMessageType", 1);
		
		verify(organisationDomainService, times(1)).searchOrganisation("testOrganisation");
		verify(deviceDomainService, times(1)).searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}