package org.opensmartgridplatform.adapter.domain.tariffswitching.application.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.tariffswitching.application.mapping.DomainTariffSwitchingMapper;
import org.opensmartgridplatform.adapter.domain.tariffswitching.infra.jms.core.OsgpCoreRequestMessageSender ;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ScheduleManagementServiceTest{
	@Mock
	private DeviceDomainService deviceDomainService;
	
	@Mock
	private DomainTariffSwitchingMapper domainCoreMapper;
	
	@Mock 
	private SsldRepository ssldRepository;
	
	@Mock
	private OrganisationDomainService organisationDomainService;
	
	@Mock
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

	@Mock
	private Device device;
	
	@Mock
	private Ssld ssld;
	
	@Mock
	private CorrelationIds correlationIds;
	
	@InjectMocks
	private ScheduleManagementService scheduleManagementService;
	
	private List<ScheduleEntry> scheduleEntries;
	private List<DeviceOutputSetting> deviceOutputSettings;
	private List<LightValue> lightValues;
	
	
	@BeforeEach
	public void setup() throws FunctionalException {
		scheduleEntries = new ArrayList<ScheduleEntry>();
		deviceOutputSettings = new ArrayList<DeviceOutputSetting>();
		lightValues = new ArrayList<LightValue>();
		
		DeviceOutputSetting dos = Mockito.mock(DeviceOutputSetting.class);
		when(dos.getOutputType()).thenReturn(RelayType.TARIFF_REVERSED);
		
		ScheduleEntry entry = Mockito.mock(ScheduleEntry.class);
		when(entry.getLightValue()).thenReturn(lightValues);
		
		LightValue value = Mockito.mock(LightValue.class);
		
		//idk how many entries are typically required
		for(int i =0; i < 3; i++) {
			scheduleEntries.add(entry);
			deviceOutputSettings.add(dos);
			lightValues.add(value);
		}
		
		
		when(device.getId()).thenReturn((long) 0.0f);
		when(ssld.getOutputSettings()).thenReturn(deviceOutputSettings);
		when(ssldRepository.findById(any())).thenReturn(Optional.of(ssld));
		
		when(deviceDomainService.searchActiveDevice(any(), any(ComponentType.class))).thenReturn(device);
	}
	
	@Test
	public void testSetTariffSchedule() throws FunctionalException {
		when(device.getDeviceType()).thenReturn(Ssld.SSLD_TYPE);
		
		scheduleManagementService.setTariffSchedule(correlationIds, scheduleEntries, (long) 0.0f, "messageType", 1);
		verify(osgpCoreRequestMessageSender, times(1)).send(any(RequestMessage.class), eq("messageType"), eq(1), eq(null), anyLong());
	}
	
	@Test
	public void testSetTariffScheduleFunctionalExceptionThrown() throws FunctionalException {
		//incorrect type should return an exception
		when(device.getDeviceType()).thenReturn(Ssld.PSLD_TYPE);
		
		assertThatThrownBy(()->{scheduleManagementService.setTariffSchedule(correlationIds, scheduleEntries, (long) 0.0f, "messageType", 1);});
		verify(osgpCoreRequestMessageSender, never()).send(any(RequestMessage.class), eq("messageType"), eq(1), eq(null));
	}
}