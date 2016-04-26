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
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDtoBuilder;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;

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
        final BundleMessageDataContainerDto dto = new BundleMessageDataContainerDto(actionDtoList);
        final BundleMessageDataContainerDto result = this.callExecutors(dto);
        Assert.assertTrue(result != null);
        this.assertResult(result);
    }

    @Test
    public void testException() {
        final List<ActionDto> actionDtoList = this.makeActions();
        final BundleMessageDataContainerDto dto = new BundleMessageDataContainerDto(actionDtoList);
        this.getStub(FindEventsQueryDto.class).failWith(new ProtocolAdapterException("simulate error"));
        final BundleMessageDataContainerDto result = this.callExecutors(dto);
        this.assertResult(result);
    }

    /**
     * Tests the retry mechanism works in the adapter-protocol. In the first run
     * a ConnectionException is thrown while executing the
     * {@link FindEventsQueryDto}. In the second attempt (when the connection is
     * restored again) the rest of the actions are executed.
     *
     * @throws ProtocolAdapterException
     *             is not thrown in this test
     */
    @Test
    public void testConnectionException() throws ProtocolAdapterException {
        final List<ActionDto> actionDtoList = this.makeActions();
        final BundleMessageDataContainerDto dto = new BundleMessageDataContainerDto(actionDtoList);

        // Set the point where to throw the ConnectionException
        this.getStub(FindEventsQueryDto.class).failWithRuntimeException(
                new ConnectionException("Connection Exception thrown!"));

        try {
            // Execute all the actions
            this.callExecutors(dto);
            Assert.fail("A ConnectionException should be thrown");
        } catch (final ConnectionException connectionException) {
            // The execution is stopped. The number of responses is equal to the
            // actions performed before the point the exception is thrown. See
            // also the order of the ArrayList in method 'makeActions'.
            Assert.assertEquals(dto.getAllResponses().size(), 8);
        }

        // Reset the point where the exception was thrown.
        this.getStub(FindEventsQueryDto.class).failWithRuntimeException(null);

        try {
            // Execute the remaining actions
            this.callExecutors(dto);
            Assert.assertEquals(dto.getAllResponses().size(), actionDtoList.size());
        } catch (final ConnectionException connectionException) {
            Assert.fail("A ConnectionException should not have been thrown.");
        }

    }

    private void assertResult(final BundleMessageDataContainerDto result) {
        Assert.assertTrue(result != null);
        Assert.assertNotNull(result != null);
        Assert.assertNotNull(result != null);
        Assert.assertNotNull(result.getActionList());
        for (final ActionDto actionDto : result.getActionList()) {
            Assert.assertNotNull(actionDto.getRequest());
            Assert.assertNotNull(actionDto.getResponse());
        }
    }

    private BundleMessageDataContainerDto callExecutors(final BundleMessageDataContainerDto dto) {
        final DlmsDevice device = new DlmsDevice();
        return this.bundleService.callExecutors(null, device, dto);
    }

    // ---- private helper methods

    private AbstractCommandExecutorStub getStub(final Class<? extends ActionRequestDto> actionRequestDto) {
        return (AbstractCommandExecutorStub) this.bundleCommandExecutorMap.getCommandExecutor(actionRequestDto);
    }

    private List<ActionDto> makeActions() {
        final List<ActionDto> actions = new ArrayList<>();
        actions.add(new ActionDto(this.builder.makeActualMeterReadsDataDtoAction()));
        actions.add(new ActionDto(this.builder.makePeriodicMeterReadsGasRequestDataDto()));
        actions.add(new ActionDto(this.builder.makePeriodicMeterReadsRequestDataDto()));
        actions.add(new ActionDto(this.builder.makeSpecialDaysRequestDataDto()));
        actions.add(new ActionDto(this.builder.makeReadAlarmRegisterDataDto()));
        actions.add(new ActionDto(this.builder.makeGetAdministrativeStatusDataDto()));
        actions.add(new ActionDto(this.builder.makeAdministrativeStatusTypeDataDto()));
        actions.add(new ActionDto(this.builder.makeActivityCalendarDataDto()));
        actions.add(new ActionDto(this.builder.makeFindEventsQueryDto()));
        actions.add(new ActionDto(this.builder.makeGMeterInfoDto()));
        actions.add(new ActionDto(this.builder.makeSetAlarmNotificationsRequestDataDto()));
        actions.add(new ActionDto(this.builder.makeSetConfigurationObjectRequestDataDto()));
        actions.add(new ActionDto(this.builder.makeSetPushSetupAlarmRequestDataDto()));
        actions.add(new ActionDto(this.builder.makeSetPushSetupSmsRequestDataDto()));
        actions.add(new ActionDto(this.builder.makeSynchronizeTimeRequestDataDto()));
        actions.add(new ActionDto(this.builder.makeGetConfigurationRequestDataDto()));
        actions.add(new ActionDto(this.builder.makeGetFirmwareVersionRequestDataDto()));
        return actions;
    }
}
