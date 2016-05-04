/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.AbstractCommandExecutorStub;
import org.osgp.adapter.protocol.dlms.domain.commands.stub.CommandExecutorMapStub;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDtoBuilder;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessagesActionListDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsRequestDataDto;

@RunWith(MockitoJUnitRunner.class)
public class BundleServiceTest {

    @InjectMocks
    private BundleService bundleService;

    private ActionDtoBuilder builder = new ActionDtoBuilder();

    @Spy
    private CommandExecutorMapStub bundleCommandExecutorMap = new CommandExecutorMapStub();

    // ------------------

    @Before
    public void setup() {
    }

    @Test
    public void testHappyFlow() throws ProtocolAdapterException {
        final List<ActionDto> actionDtoList = this.makeActions();
        final BundleMessagesActionListDto dto = new BundleMessagesActionListDto(actionDtoList);
        final List<ActionResponseDto> result = this.callExecutors(dto);
        Assert.assertTrue(result != null);
        Assert.assertEquals(actionDtoList.size(), result.size());
    }

    @Test
    public void testException() {
        final List<ActionDto> actionDtoList = this.makeActions();
        final BundleMessagesActionListDto dto = new BundleMessagesActionListDto(actionDtoList);
        this.getStub(FindEventsRequestDataDto.class).failWith(new ProtocolAdapterException("simulate error"));
        final List<ActionResponseDto> result = this.callExecutors(dto);
        Assert.assertTrue(result != null);
        Assert.assertEquals(actionDtoList.size(), result.size());
    }

    private List<ActionResponseDto> callExecutors(final BundleMessagesActionListDto dto) {
        final DlmsDevice device = new DlmsDevice();
        return this.bundleService.callExecutors(null, device, dto);
    }

    // ---- private helper methods

    private AbstractCommandExecutorStub getStub(final Class<? extends ActionDto> aActionDto) {
        return (AbstractCommandExecutorStub) this.bundleCommandExecutorMap.getCommandExecutor(aActionDto);
    }

    private List<ActionDto> makeActions() {
        final List<ActionDto> actions = new ArrayList<>();
        actions.add(this.builder.makeFindEventsQueryDto());
        actions.add(this.builder.makeActualMeterReadsDataDtoAction());
        actions.add(this.builder.makePeriodicMeterReadsGasRequestDataDto());
        actions.add(this.builder.makePeriodicMeterReadsRequestDataDto());
        actions.add(this.builder.makeSpecialDaysRequestDataDto());
        actions.add(this.builder.makeReadAlarmRegisterDataDto());
        actions.add(this.builder.makeGetAdministrativeStatusDataDto());
        actions.add(this.builder.makeAdministrativeStatusTypeDataDto());
        actions.add(this.builder.makeActivityCalendarDataDto());
        actions.add(this.builder.makeGMeterInfoDto());
        actions.add(this.builder.makeSetAlarmNotificationsRequestDataDto());
        actions.add(this.builder.makeSetConfigurationObjectRequestDataDto());
        actions.add(this.builder.makeSetPushSetupAlarmRequestDataDto());
        actions.add(this.builder.mkeSetPushSetupSmsRequestDataDto());
        actions.add(this.builder.makeSynchronizeTimeRequestDataDto());
        actions.add(this.builder.makeGetConfigurationRequestDataDto());
        actions.add(this.builder.makeGetFirmwareVersionRequestDataDto());
        return actions;
    }

}
