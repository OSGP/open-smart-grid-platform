/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsBundleGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetAdministrativeStatusBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetFirmwareVersionsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsGasBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReadAlarmRegisterBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveConfigurationObjectsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveEventsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetActivityCalendarBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAdministrativeStatusBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAlarmNotificationsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetConfigurationObjectBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetEncryptionKeyExchangeOnGMeterBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetPushSetupAlarmBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetPushSetupSmsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetSpecialDaysBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SynchronizeTimeBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.AbstractCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.GetActualMeterReadsBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.GetActualMeterReadsBundleGasCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.GetAdministrativeStatusBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.GetFirmwareVersionsBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.GetPeriodicMeterReadsBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.GetPeriodicMeterReadsGasBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.ReadAlarmRegisterBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.RetrieveConfigurationObjectsBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.RetrieveEventsBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.SetActivityCalendarBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.SetAdministrativeStatusBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.SetAlarmNotificationsBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.SetConfigurationObjectBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.SetEncryptionKeyExchangeOnGMeterBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.SetPushSetupAlarmBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.SetPushSetupSmsBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.SetSpecialDaysBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.SynchronizeTimeBundleCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDtoBuilder;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataGasDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GMeterInfoDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetAlarmNotificationsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupAlarmRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupSmsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequestDataDto;

@RunWith(MockitoJUnitRunner.class)
public class BundleServiceTest {

    @InjectMocks
    private BundleService bundleService;

    private ActionDtoBuilder builder = new ActionDtoBuilder();
 
    private static Map<Class<? extends ActionDto>, AbstractCommandExecutorStub> stubs = new HashMap<Class<? extends ActionDto>, AbstractCommandExecutorStub>();

    @Spy
    private RetrieveEventsBundleCommandExecutor retrieveEventsBundleCommandExecutor = new RetrieveEventsBundleCommandExecutorStub();
    @Spy
    private GetActualMeterReadsBundleCommandExecutor actualMeterReadsBundleCommandExecutor = new GetActualMeterReadsBundleCommandExecutorStub();
    @Spy
    private GetActualMeterReadsBundleGasCommandExecutor actualMeterReadsBundleGasCommandExecutor = new GetActualMeterReadsBundleGasCommandExecutorStub();
    @Spy
    private GetPeriodicMeterReadsGasBundleCommandExecutor getPeriodicMeterReadsGasBundleCommandExecutor = new GetPeriodicMeterReadsGasBundleCommandExecutorStub();
    @Spy
    private GetPeriodicMeterReadsBundleCommandExecutor getPeriodicMeterReadsBundleCommandExecutor = new GetPeriodicMeterReadsBundleCommandExecutorStub();
    @Spy
    private SetSpecialDaysBundleCommandExecutor setSpecialDaysBundleCommandExecutor = new SetSpecialDaysBundleCommandExecutorStub();
    @Spy
    private ReadAlarmRegisterBundleCommandExecutor readAlarmRegisterBundleCommandExecutor = new ReadAlarmRegisterBundleCommandExecutorStub();
    @Spy
    private GetAdministrativeStatusBundleCommandExecutor getAdministrativeStatusBundleCommandExecutor = new GetAdministrativeStatusBundleCommandExecutorStub();
    @Spy
    private SetAdministrativeStatusBundleCommandExecutor setAdministrativeStatusBundleCommandExecutor = new SetAdministrativeStatusBundleCommandExecutorStub();
    @Spy
    private SetActivityCalendarBundleCommandExecutor setActivityCalendarBundleCommandExecutor = new SetActivityCalendarBundleCommandExecutorStub();
    @Spy
    private SetEncryptionKeyExchangeOnGMeterBundleCommandExecutor setEncryptionKeyExchangeOnGMeterBundleCommandExecutor = new SetEncryptionKeyExchangeOnGMeterBundleCommandExecutorStub();
    @Spy
    private SetAlarmNotificationsBundleCommandExecutor setAlarmNotificationsBundleCommandExecutor = new SetAlarmNotificationsBundleCommandExecutorStub();
    @Spy
    private SetConfigurationObjectBundleCommandExecutor setConfigurationObjectBundleCommandExecutor = new SetConfigurationObjectBundleCommandExecutorStub();
    @Spy
    private SetPushSetupAlarmBundleCommandExecutor setPushSetupAlarmBundleCommandExecutor = new SetPushSetupAlarmBundleCommandExecutorStub();
    @Spy
    private SetPushSetupSmsBundleCommandExecutor setPushSetupSmsBundleCommandExecutor = new SetPushSetupSmsBundleCommandExecutorStub();
    @Spy
    private SynchronizeTimeBundleCommandExecutor synchronizeTimeBundleCommandExecutor = new SynchronizeTimeBundleCommandExecutorStub();
    @Spy
    private RetrieveConfigurationObjectsBundleCommandExecutor retrieveConfigurationObjectsBundleCommandExecutor = new RetrieveConfigurationObjectsBundleCommandExecutorStub();
    @Spy
    private GetFirmwareVersionsBundleCommandExecutor getFirmwareVersionsBundleCommandExecutor = new GetFirmwareVersionsBundleCommandExecutorStub();

    // ------------------

    @Before
    public void setup() {
        stubs.put(FindEventsQueryDto.class, (AbstractCommandExecutorStub) retrieveEventsBundleCommandExecutor);
        stubs.put(ActualMeterReadsDataDto.class, (AbstractCommandExecutorStub) actualMeterReadsBundleCommandExecutor);
        stubs.put(ActualMeterReadsDataGasDto.class,
                (AbstractCommandExecutorStub) actualMeterReadsBundleGasCommandExecutor);
        stubs.put(SpecialDaysRequestDataDto.class, (AbstractCommandExecutorStub) setSpecialDaysBundleCommandExecutor);
        stubs.put(ReadAlarmRegisterDataDto.class, (AbstractCommandExecutorStub) readAlarmRegisterBundleCommandExecutor);
        stubs.put(GetAdministrativeStatusDataDto.class,
                (AbstractCommandExecutorStub) getAdministrativeStatusBundleCommandExecutor);
        stubs.put(PeriodicMeterReadsRequestDataDto.class,
                (AbstractCommandExecutorStub) getPeriodicMeterReadsBundleCommandExecutor);
        stubs.put(PeriodicMeterReadsGasRequestDataDto.class,
                (AbstractCommandExecutorStub) getPeriodicMeterReadsGasBundleCommandExecutor);
        stubs.put(AdministrativeStatusTypeDataDto.class,
                (AbstractCommandExecutorStub) setAdministrativeStatusBundleCommandExecutor);
        stubs.put(ActivityCalendarDataDto.class, (AbstractCommandExecutorStub) setActivityCalendarBundleCommandExecutor);
        stubs.put(GMeterInfoDto.class,
                (AbstractCommandExecutorStub) setEncryptionKeyExchangeOnGMeterBundleCommandExecutor);
        stubs.put(SetAlarmNotificationsRequestDataDto.class,
                (AbstractCommandExecutorStub) setAlarmNotificationsBundleCommandExecutor);
        stubs.put(SetConfigurationObjectRequestDataDto.class,
                (AbstractCommandExecutorStub) setConfigurationObjectBundleCommandExecutor);
        stubs.put(SetPushSetupAlarmRequestDataDto.class,
                (AbstractCommandExecutorStub) setPushSetupAlarmBundleCommandExecutor);
        stubs.put(SetPushSetupSmsRequestDataDto.class,
                (AbstractCommandExecutorStub) setPushSetupSmsBundleCommandExecutor);
        stubs.put(SynchronizeTimeRequestDataDto.class,
                (AbstractCommandExecutorStub) synchronizeTimeBundleCommandExecutor);
        stubs.put(GetConfigurationRequestDataDto.class,
                (AbstractCommandExecutorStub) retrieveConfigurationObjectsBundleCommandExecutor);
        stubs.put(GetFirmwareVersionRequestDataDto.class,
                (AbstractCommandExecutorStub) getFirmwareVersionsBundleCommandExecutor);
    }

    @Test
    public void testHappyFlow() throws ProtocolAdapterException {
        final List<ActionDto> actionDtoList = this.makeActions();
        BundleMessageDataContainerDto dto = new BundleMessageDataContainerDto(actionDtoList);
        List<ActionResponseDto> result = callExecutors(dto);
        Assert.assertTrue(result != null);
        Assert.assertEquals(actionDtoList.size(), result.size());
    }

    @Test
    public void testException() {
        final List<ActionDto> actionDtoList = this.makeActions();
        BundleMessageDataContainerDto dto = new BundleMessageDataContainerDto(actionDtoList);
        getStub(FindEventsQueryDto.class).failWith(new ProtocolAdapterException("simulate error"));
        List<ActionResponseDto> result = callExecutors(dto);
        Assert.assertTrue(result != null);
        Assert.assertEquals(actionDtoList.size(), result.size());
    }

    private List<ActionResponseDto> callExecutors(BundleMessageDataContainerDto dto) {
        final DlmsDevice device = new DlmsDevice();
        return bundleService.callExecutors(null, device, dto);
    }

    // ---- private helper methods

    private AbstractCommandExecutorStub getStub(Class<?> aActionDto) {
        return stubs.get(aActionDto);
    }

    private List<ActionDto> makeActions() {
        List<ActionDto> actions = new ArrayList<>();
        actions.add(builder.makeFindEventsQueryDto());
        actions.add(builder.makeActualMeterReadsDataDtoAction());
        actions.add(builder.makePeriodicMeterReadsGasRequestDataDto());
        actions.add(builder.makePeriodicMeterReadsRequestDataDto());
        actions.add(builder.makeSpecialDaysRequestDataDto());
        actions.add(builder.makeReadAlarmRegisterDataDto());
        actions.add(builder.makeGetAdministrativeStatusDataDto());
        actions.add(builder.makeAdministrativeStatusTypeDataDto());
        actions.add(builder.makeActivityCalendarDataDto());
        actions.add(builder.makeGMeterInfoDto());
        actions.add(builder.makeSetAlarmNotificationsRequestDataDto());
        actions.add(builder.makeSetConfigurationObjectRequestDataDto());
        actions.add(builder.makeSetPushSetupAlarmRequestDataDto());
        actions.add(builder.mkeSetPushSetupSmsRequestDataDto());
        actions.add(builder.makeSynchronizeTimeRequestDataDto());
        actions.add(builder.makeGetConfigurationRequestDataDto());
        actions.add(builder.makeGetFirmwareVersionRequestDataDto());
        return actions;
    }

 }
