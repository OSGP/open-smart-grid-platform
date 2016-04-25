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
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDtoBuilder;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
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

    private static Map<Class<? extends ActionRequestDto>, AbstractCommandExecutorStub> STUBS = new HashMap<Class<? extends ActionRequestDto>, AbstractCommandExecutorStub>();

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
        if (STUBS.isEmpty()) {
            this.fillStubs();
        }
    }

    @Test
    public void testHappyFlow() throws ProtocolAdapterException {
        final List<ActionDto> actionDtoList = this.makeActions();
        final BundleMessageDataContainerDto dto = new BundleMessageDataContainerDto(actionDtoList);
        final BundleMessageDataContainerDto result = this.callExecutors(dto);
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

    private AbstractCommandExecutorStub getStub(final Class<?> aActionDto) {
        return STUBS.get(aActionDto);
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

    private void fillStubs() {
        STUBS.put(FindEventsQueryDto.class, (AbstractCommandExecutorStub) this.retrieveEventsBundleCommandExecutor);
        STUBS.put(ActualMeterReadsDataDto.class,
                (AbstractCommandExecutorStub) this.actualMeterReadsBundleCommandExecutor);
        STUBS.put(ActualMeterReadsDataGasDto.class,
                (AbstractCommandExecutorStub) this.actualMeterReadsBundleGasCommandExecutor);
        STUBS.put(SpecialDaysRequestDataDto.class,
                (AbstractCommandExecutorStub) this.setSpecialDaysBundleCommandExecutor);
        STUBS.put(ReadAlarmRegisterDataDto.class,
                (AbstractCommandExecutorStub) this.readAlarmRegisterBundleCommandExecutor);
        STUBS.put(GetAdministrativeStatusDataDto.class,
                (AbstractCommandExecutorStub) this.getAdministrativeStatusBundleCommandExecutor);
        STUBS.put(PeriodicMeterReadsRequestDataDto.class,
                (AbstractCommandExecutorStub) this.getPeriodicMeterReadsBundleCommandExecutor);
        STUBS.put(PeriodicMeterReadsGasRequestDataDto.class,
                (AbstractCommandExecutorStub) this.getPeriodicMeterReadsGasBundleCommandExecutor);
        STUBS.put(AdministrativeStatusTypeDataDto.class,
                (AbstractCommandExecutorStub) this.setAdministrativeStatusBundleCommandExecutor);
        STUBS.put(ActivityCalendarDataDto.class,
                (AbstractCommandExecutorStub) this.setActivityCalendarBundleCommandExecutor);
        STUBS.put(GMeterInfoDto.class,
                (AbstractCommandExecutorStub) this.setEncryptionKeyExchangeOnGMeterBundleCommandExecutor);
        STUBS.put(SetAlarmNotificationsRequestDataDto.class,
                (AbstractCommandExecutorStub) this.setAlarmNotificationsBundleCommandExecutor);
        STUBS.put(SetConfigurationObjectRequestDataDto.class,
                (AbstractCommandExecutorStub) this.setConfigurationObjectBundleCommandExecutor);
        STUBS.put(SetPushSetupAlarmRequestDataDto.class,
                (AbstractCommandExecutorStub) this.setPushSetupAlarmBundleCommandExecutor);
        STUBS.put(SetPushSetupSmsRequestDataDto.class,
                (AbstractCommandExecutorStub) this.setPushSetupSmsBundleCommandExecutor);
        STUBS.put(SynchronizeTimeRequestDataDto.class,
                (AbstractCommandExecutorStub) this.synchronizeTimeBundleCommandExecutor);
        STUBS.put(GetConfigurationRequestDataDto.class,
                (AbstractCommandExecutorStub) this.retrieveConfigurationObjectsBundleCommandExecutor);
        STUBS.put(GetFirmwareVersionRequestDataDto.class,
                (AbstractCommandExecutorStub) this.getFirmwareVersionsBundleCommandExecutor);
    }
}
