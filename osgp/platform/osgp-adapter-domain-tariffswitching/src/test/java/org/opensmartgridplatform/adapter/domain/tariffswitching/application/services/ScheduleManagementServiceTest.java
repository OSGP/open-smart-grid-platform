/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.tariffswitching.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.tariffswitching.application.mapping.DomainTariffSwitchingMapper;
import org.opensmartgridplatform.adapter.domain.tariffswitching.infra.jms.core.OsgpCoreRequestMessageSender;
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

@ExtendWith(MockitoExtension.class)
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

	@Captor
	final ArgumentCaptor<RequestMessage> requestMessageArgumentCaptor =
			ArgumentCaptor.forClass(RequestMessage.class);
	@Captor
	final ArgumentCaptor<Long> scheduleTimeCaptor = ArgumentCaptor.forClass(long.class);
	@Captor
	final ArgumentCaptor<String> ipCaptor = ArgumentCaptor.forClass(String.class);
	@Captor
	final ArgumentCaptor<String> messageTypeCaptor = ArgumentCaptor.forClass(String.class);
	@Captor
	final ArgumentCaptor<Integer> priorityCapture = ArgumentCaptor.forClass(int.class);
	
	private List<ScheduleEntry> scheduleEntries;
	private List<DeviceOutputSetting> deviceOutputSettings;
	private List<LightValue> lightValues;
	private static final long SCHEDULE_TIME = 0L;
	private static final String MESSAGE_TYPE = "testType";
	private static final int PRIORITY = 1;


	@BeforeEach
	private void setup() throws FunctionalException {
		when(this.device.getDeviceType()).thenReturn(Ssld.SSLD_TYPE);
		when(this.deviceDomainService.searchActiveDevice(any(), any(ComponentType.class))).thenReturn(this.device);
	}
	
	@Test
	void testSetTariffSchedule() throws FunctionalException {
		this.arrangeTestSetTariffSchedule();
		this.scheduleManagementService.setTariffSchedule(this.correlationIds, this.scheduleEntries, SCHEDULE_TIME,
				MESSAGE_TYPE, PRIORITY);

		verify(this.osgpCoreRequestMessageSender).send(this.requestMessageArgumentCaptor.capture(),
				this.messageTypeCaptor.capture(), this.priorityCapture.capture(), this.ipCaptor.capture(),
				this.scheduleTimeCaptor.capture());

		assertThat(MESSAGE_TYPE).isEqualTo(this.messageTypeCaptor.getValue());
		assertThat(SCHEDULE_TIME).isEqualTo(this.scheduleTimeCaptor.getValue());
		assertThat(PRIORITY).isEqualTo(this.priorityCapture.getValue());
	}

	private void arrangeTestSetTariffSchedule() {
		this.scheduleEntries = new ArrayList<>();
		this.deviceOutputSettings = new ArrayList<>();
		this.lightValues = new ArrayList<>();

		final DeviceOutputSetting deviceOutputSetting = Mockito.mock(DeviceOutputSetting.class);
		when(deviceOutputSetting.getOutputType()).thenReturn(RelayType.TARIFF_REVERSED);

		final ScheduleEntry entry = Mockito.mock(ScheduleEntry.class);
		when(entry.getLightValue()).thenReturn(this.lightValues);

		final LightValue value = Mockito.mock(LightValue.class);

		//idk how many entries are typically required
		for(int i =0; i < 3; i++) {
			this.scheduleEntries.add(entry);
			this.deviceOutputSettings.add(deviceOutputSetting);
			this.lightValues.add(value);
		}

		when(this.device.getIpAddress()).thenReturn("127.0.0.1");
		when(this.device.getId()).thenReturn(0L);
		when(this.ssld.getOutputSettings()).thenReturn(this.deviceOutputSettings);
		when(this.ssldRepository.findById(any())).thenReturn(Optional.of(this.ssld));
	}
	
	@Test
	void testSetTariffScheduleFunctionalExceptionThrown() {
		//incorrect type should return an exception
		when(this.device.getDeviceType()).thenReturn(Ssld.PSLD_TYPE);
		assertThatThrownBy(()-> this.scheduleManagementService.setTariffSchedule(this.correlationIds, this.scheduleEntries,
				SCHEDULE_TIME, MESSAGE_TYPE, PRIORITY)).isInstanceOf(FunctionalException.class);
		verify(this.osgpCoreRequestMessageSender, never()).send(any(RequestMessage.class), eq("messageType"), eq(1), eq(null));
	}
}
