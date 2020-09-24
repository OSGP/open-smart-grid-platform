package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.application.mapping.DomainCoreMapper;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatusMapped;
import org.opensmartgridplatform.domain.core.valueobjects.LightType;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.LinkType;
import org.opensmartgridplatform.domain.core.valueobjects.TariffValue;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.NoDeviceResponseException;
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

	@Captor
	private ArgumentCaptor<ResponseMessage> argumentResM;

	@Captor
	private ArgumentCaptor<RequestMessage> argumentReqM;

	@Captor
	private ArgumentCaptor<String> argumentStringOne;

	@Captor
	private ArgumentCaptor<String> argumentStringTwo;

	@Captor
	private ArgumentCaptor<Integer> argumentInt;
	
	@Test
	public void testGetStatusTestDeviceTypeIsNotLmdType() throws FunctionalException {
		final Device device = Mockito.mock(Device.class);
		when(device.getIpAddress()).thenReturn("testIp");
		when(this.deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(device);
		
		this.deviceInstallationService.getStatus("testOrganisation", "testDevice", "testUid", "testMessageType", 1);

		verify(this.osgpCoreRequestMessageSender).send(this.argumentReqM.capture(), this.argumentStringOne.capture(),
				this.argumentInt.capture(), this.argumentStringTwo.capture());

		assertThat(this.argumentReqM.getValue()).usingRecursiveComparison().isEqualTo(new RequestMessage(
				"testUid",
				"testOrganisation",
				"testDevice", null));
		assertThat(this.argumentStringOne.getValue()).isEqualTo("testMessageType");
		assertThat(this.argumentInt.getValue()).isEqualTo(1);
		assertThat(this.argumentStringTwo.getValue()).isEqualTo("testIp");
	}
	
	@Test
	public void testGetStatusTestDeviceTypeIsLmdType() throws FunctionalException {
		final Device device = Mockito.mock(Device.class);
		when(device.getDeviceType()).thenReturn("LMD");
		when(device.getIpAddress()).thenReturn("testIp");
		when(this.deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(device);

		this.deviceInstallationService.getStatus("testOrganisation", "testDevice", "testUid", "testMessageType", 1);

		verify(this.osgpCoreRequestMessageSender).send(this.argumentReqM.capture(), this.argumentStringOne.capture(),
				this.argumentInt.capture(), this.argumentStringTwo.capture());

		assertThat(this.argumentReqM.getValue()).usingRecursiveComparison().isEqualTo(new RequestMessage(
				"testUid",
				"testOrganisation",
				"testDevice", null));
		assertThat(this.argumentStringOne.getValue()).isEqualTo("GET_LIGHT_SENSOR_STATUS");
		assertThat(this.argumentInt.getValue()).isEqualTo(1);
		assertThat(this.argumentStringTwo.getValue()).isEqualTo("testIp");
	}

	@Test
	public void testHandleGetStatusResponseNotOk() {
		this.deviceInstallationService.handleGetStatusResponse(null, new CorrelationIds("testOrganisation", "testDevice", "testUid"),
				"testMessageType", 1, ResponseMessageResultType.NOT_OK, null);

		verify(this.webServiceResponseMessageSender).send(this.argumentResM.capture());
		assertThat(this.argumentResM.getValue()).usingRecursiveComparison().isEqualTo(ResponseMessage.newResponseMessageBuilder()
				.withIds(new CorrelationIds("testOrganisation", "testDevice", "testUid"))
				.withResult(ResponseMessageResultType.NOT_OK).withOsgpException(null)
				.withDataObject(null).withMessagePriority(1).build());
	}

	@Test
	public void testHandleGetStatusResponseOkLmdStatusNotNull() throws FunctionalException, ValidationException {
		final TariffValue editedTariffValue = new TariffValue();
		editedTariffValue.setHigh(true);
		editedTariffValue.setIndex(10);
		final DeviceStatusMapped deviceStatus = new DeviceStatusMapped(null,
				Arrays.asList(new LightValue(0, true, 50), new LightValue(1, true, 75), new LightValue(2, false, 0)),
				LinkType.ETHERNET, LinkType.GPRS, LightType.ONE_TO_TEN_VOLT,0
		);
		when(this.domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(deviceStatus);

		final Device mockedDevice = Mockito.mock(Device.class);
		when(mockedDevice.getDeviceType()).thenReturn(LightMeasurementDevice.LMD_TYPE);
		when(this.deviceDomainService.searchDevice("testDevice")).thenReturn(mockedDevice);

		this.deviceInstallationService.handleGetStatusResponse(null, new CorrelationIds("testOrganisation", "testDevice", "testUid"),
				"testMessageType", 1, ResponseMessageResultType.OK, null);

		verify(this.webServiceResponseMessageSender).send(this.argumentResM.capture());
		assertThat(this.argumentResM.getValue()).usingRecursiveComparison().isEqualTo(ResponseMessage.newResponseMessageBuilder()
				.withIds(new CorrelationIds("testOrganisation", "testDevice", "testUid"))
				.withResult(ResponseMessageResultType.OK).withOsgpException(null)
				.withDataObject(deviceStatus)
				.withMessagePriority(1)
				.build());
	}

	@Test
	public void testHandleGetStatusResponseOkLmdStatusNull() throws FunctionalException {
		when(this.domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(null);

		final Device mockedDevice = Mockito.mock(Device.class);
		when(mockedDevice.getDeviceType()).thenReturn(LightMeasurementDevice.LMD_TYPE);
		when(this.deviceDomainService.searchDevice("testDevice")).thenReturn(mockedDevice);

		this.deviceInstallationService.handleGetStatusResponse(null, new CorrelationIds("testOrganisation", "testDevice", "testUid"),
				"testMessageType", 1, ResponseMessageResultType.OK, null);

		verify(this.webServiceResponseMessageSender).send(this.argumentResM.capture());
		assertThat(this.argumentResM.getValue()).usingRecursiveComparison().isEqualTo(ResponseMessage.newResponseMessageBuilder()
				.withIds(new CorrelationIds("testOrganisation", "testDevice", "testUid"))
				.withResult(ResponseMessageResultType.NOT_OK)
				.withOsgpException(new TechnicalException(ComponentType.DOMAIN_CORE,
						"Light measurement device was not able to report light sensor status",
						new NoDeviceResponseException()))
				.withDataObject(null)
				.withMessagePriority(1).build());
	}

	@Test
	public void testHandleGetStatusResponseOkNotLmdStatusNotNull() throws FunctionalException {
		final DeviceStatus mockedStatus = Mockito.mock(DeviceStatus.class);
		when(mockedStatus.getLightValues()).thenReturn(Collections.emptyList());
		when(this.domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(mockedStatus);

		final Device mockedDevice = Mockito.mock(Device.class);
		when(mockedDevice.getDeviceType()).thenReturn(null);
		when(this.deviceDomainService.searchDevice("testDevice")).thenReturn(mockedDevice);

		when(this.ssldRepository.findByDeviceIdentification("testDevice")).thenReturn(new Ssld());

		this.deviceInstallationService.handleGetStatusResponse(null, new CorrelationIds("testOrganisation", "testDevice", "testUid"),
				"testMessageType", 1, ResponseMessageResultType.OK, null);

		verify(this.webServiceResponseMessageSender).send(this.argumentResM.capture());
		assertThat(this.argumentResM.getValue()).usingRecursiveComparison().ignoringFields("dataObject").isEqualTo(ResponseMessage.newResponseMessageBuilder()
				.withIds(new CorrelationIds("testOrganisation", "testDevice", "testUid"))
				.withResult(ResponseMessageResultType.OK)
				.withOsgpException(null)
				.withDataObject(null)
				.withMessagePriority(1).build());
	}

	@Test
	public void testHandleGetStatusResponseOkNotLMDStatusNull() throws FunctionalException {
		when(this.domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(null);

		final Device mockedDevice = Mockito.mock(Device.class);
		when(mockedDevice.getDeviceType()).thenReturn(null);
		when(this.deviceDomainService.searchDevice("testDevice")).thenReturn(mockedDevice);

		when(this.ssldRepository.findByDeviceIdentification("testDevice")).thenReturn(new Ssld());

		this.deviceInstallationService.handleGetStatusResponse(null, new CorrelationIds("testOrganisation", "testDevice", "testUid"),
				"testMessageType", 1, ResponseMessageResultType.OK, null);

		verify(this.webServiceResponseMessageSender).send(this.argumentResM.capture());
		assertThat(this.argumentResM.getValue()).usingRecursiveComparison().ignoringFields("dataObject").isEqualTo(ResponseMessage.newResponseMessageBuilder()
				.withIds(new CorrelationIds("testOrganisation", "testDevice", "testUid"))
				.withResult(ResponseMessageResultType.NOT_OK)
				.withOsgpException(new TechnicalException(ComponentType.DOMAIN_CORE,
						"SSLD was not able to report relay status",
						new NoDeviceResponseException()))
				.withDataObject(null)
				.withMessagePriority(1).build());
	}
	
	@Test
	public void testStartSelfTest() throws FunctionalException {
		final Device device = Mockito.mock(Device.class);
		when(device.getIpAddress()).thenReturn("testIp");
		when(this.deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(device);
		
		this.deviceInstallationService.startSelfTest("testDevice", "testOrganisation", "testUid",
				"testMessageType", 1);

		verify(this.osgpCoreRequestMessageSender).send(this.argumentReqM.capture(), this.argumentStringOne.capture(),
				this.argumentInt.capture(), this.argumentStringTwo.capture());

		assertThat(this.argumentReqM.getValue()).usingRecursiveComparison().isEqualTo(new RequestMessage(
				"testUid",
				"testOrganisation",
				"testDevice", null));
		assertThat(this.argumentStringOne.getValue()).isEqualTo("testMessageType");
		assertThat(this.argumentInt.getValue()).isEqualTo(1);
		assertThat(this.argumentStringTwo.getValue()).isEqualTo("testIp");
	}
	
	@Test
	public void testStopSelfTest() throws FunctionalException {
		final Device device = Mockito.mock(Device.class);
		when(device.getIpAddress()).thenReturn("testIp");
		when(this.deviceDomainService.searchActiveDevice("testDevice", ComponentType.DOMAIN_CORE)).thenReturn(device);
		
		this.deviceInstallationService.stopSelfTest("testDevice", "testOrganisation", "testUid",
				"testMessageType", 1);

		verify(this.osgpCoreRequestMessageSender).send(this.argumentReqM.capture(), this.argumentStringOne.capture(),
				this.argumentInt.capture(), this.argumentStringTwo.capture());

		assertThat(this.argumentReqM.getValue()).usingRecursiveComparison().isEqualTo(new RequestMessage(
				"testUid",
				"testOrganisation",
				"testDevice", null));
		assertThat(this.argumentStringOne.getValue()).isEqualTo("testMessageType");
		assertThat(this.argumentInt.getValue()).isEqualTo(1);
		assertThat(this.argumentStringTwo.getValue()).isEqualTo("testIp");
	}
}