/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.CommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsBundleGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetAdministrativeStatusBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetFirmwareVersionsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsGasBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReadAlarmRegisterBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReplaceKeyBundleCommandExecutorImpl;
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
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
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
import com.alliander.osgp.dto.valueobjects.smartmetering.KeySetDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetAlarmNotificationsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupAlarmRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupSmsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequestDataDto;

@Service(value = "dlmsBundleService")
public class BundleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

    @Autowired
    private RetrieveEventsBundleCommandExecutor retrieveEventsBundleCommandExecutor;

    @Autowired
    private GetActualMeterReadsBundleCommandExecutor actualMeterReadsBundleCommandExecutor;

    @Autowired
    private GetActualMeterReadsBundleGasCommandExecutor actualMeterReadsBundleGasCommandExecutor;

    @Autowired
    private GetPeriodicMeterReadsGasBundleCommandExecutor getPeriodicMeterReadsGasBundleCommandExecutor;

    @Autowired
    private GetPeriodicMeterReadsBundleCommandExecutor getPeriodicMeterReadsBundleCommandExecutor;

    @Autowired
    private SetSpecialDaysBundleCommandExecutor setSpecialDaysBundleCommandExecutor;

    @Autowired
    private ReadAlarmRegisterBundleCommandExecutor readAlarmRegisterBundleCommandExecutor;

    @Autowired
    private GetAdministrativeStatusBundleCommandExecutor getAdministrativeStatusBundleCommandExecutor;

    @Autowired
    private SetAdministrativeStatusBundleCommandExecutor setAdministrativeStatusBundleCommandExecutor;

    @Autowired
    private SetActivityCalendarBundleCommandExecutor setActivityCalendarBundleCommandExecutor;

    @Autowired
    private SetEncryptionKeyExchangeOnGMeterBundleCommandExecutor setEncryptionKeyExchangeOnGMeterBundleCommandExecutor;

    @Autowired
    private SetAlarmNotificationsBundleCommandExecutor setAlarmNotificationsBundleCommandExecutor;

    @Autowired
    private SetConfigurationObjectBundleCommandExecutor setConfigurationObjectBundleCommandExecutor;

    @Autowired
    private SetPushSetupAlarmBundleCommandExecutor setPushSetupAlarmBundleCommandExecutor;

    @Autowired
    private SetPushSetupSmsBundleCommandExecutor setPushSetupSmsBundleCommandExecutor;

    @Autowired
    private SynchronizeTimeBundleCommandExecutor synchronizeTimeBundleCommandExecutor;

    @Autowired
    private RetrieveConfigurationObjectsBundleCommandExecutor retrieveConfigurationObjectsBundleCommandExecutor;

    @Autowired
    private GetFirmwareVersionsBundleCommandExecutor getFirmwareVersionsBundleCommandExecutor;

    @Autowired
    private ReplaceKeyBundleCommandExecutorImpl replaceKeyBundleCommandExecutor;

    private final static Map<Class<? extends ActionRequestDto>, CommandExecutor<? extends ActionRequestDto, ? extends ActionResponseDto>> CLAZZ_EXECUTOR_MAP = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        CLAZZ_EXECUTOR_MAP.put(FindEventsQueryDto.class, this.retrieveEventsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(ActualMeterReadsDataDto.class, this.actualMeterReadsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(ActualMeterReadsDataGasDto.class, this.actualMeterReadsBundleGasCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(SpecialDaysRequestDataDto.class, this.setSpecialDaysBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(ReadAlarmRegisterDataDto.class, this.readAlarmRegisterBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(GetAdministrativeStatusDataDto.class, this.getAdministrativeStatusBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(PeriodicMeterReadsRequestDataDto.class, this.getPeriodicMeterReadsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(PeriodicMeterReadsGasRequestDataDto.class,
                this.getPeriodicMeterReadsGasBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP
                .put(AdministrativeStatusTypeDataDto.class, this.setAdministrativeStatusBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(ActivityCalendarDataDto.class, this.setActivityCalendarBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(GMeterInfoDto.class, this.setEncryptionKeyExchangeOnGMeterBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(SetAlarmNotificationsRequestDataDto.class,
                this.setAlarmNotificationsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(SetConfigurationObjectRequestDataDto.class,
                this.setConfigurationObjectBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(SetPushSetupAlarmRequestDataDto.class, this.setPushSetupAlarmBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(SetPushSetupSmsRequestDataDto.class, this.setPushSetupSmsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(SynchronizeTimeRequestDataDto.class, this.synchronizeTimeBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(GetConfigurationRequestDataDto.class,
                this.retrieveConfigurationObjectsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(GetFirmwareVersionRequestDataDto.class, this.getFirmwareVersionsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(KeySetDto.class, this.replaceKeyBundleCommandExecutor);
    }

    public BundleMessageDataContainerDto callExecutors(final ClientConnection conn, final DlmsDevice device,
            final BundleMessageDataContainerDto bundleMessageDataContainerDto) {

        if (CLAZZ_EXECUTOR_MAP.isEmpty()) {
            this.postConstruct();
        }

        final List<ActionDto> actionList = bundleMessageDataContainerDto.getActionList();
        for (final ActionDto actionDto : actionList) {

            // Only execute the request when there is no response available yet.
            // Because it could be a retry.
            if (actionDto.getResponse() == null) {

                // suppress else the compiler will complain
                @SuppressWarnings({ "unchecked" })
                final CommandExecutor<ActionRequestDto, ActionResponseDto> executor = (CommandExecutor<ActionRequestDto, ActionResponseDto>) CLAZZ_EXECUTOR_MAP
                        .get(actionDto.getRequest().getClass());

                try {
                    LOGGER.debug("**************************************************");
                    LOGGER.info("Calling executor in bundle {}", executor.getClass().getSimpleName());
                    LOGGER.debug("**************************************************");
                    actionDto.setResponse(executor.execute(conn, device, actionDto.getRequest()));
                } catch (final ConnectionException connectionException) {
                    LOGGER.error("Warning: A connection exception occurred while executing "
                            + executor.getClass().getSimpleName(), connectionException);

                    final List<ActionDto> remainingActionDtoList = actionList.subList(actionList.indexOf(actionDto),
                            actionList.size());

                    for (final ActionDto remainingActionDto : remainingActionDtoList) {
                        LOGGER.debug("Skipping: {}", remainingActionDto.getRequest().getClass().getSimpleName());
                    }

                    actionDto.setResponse(null);
                    throw connectionException;
                } catch (final Exception exception) {
                    LOGGER.error("Error while executing bundle action "
                            + actionDto.getRequest().getClass().getSimpleName() + " for class "
                            + actionDto.getRequest().getClass().getSimpleName() + " and executor "
                            + executor.getClass().getSimpleName(), exception);
                    actionDto.setResponse(new ActionResponseDto(exception, "Error while executing bundle action for class "
                            + actionDto.getRequest().getClass().getSimpleName() + " and executor "
                            + executor.getClass().getSimpleName()));
                }
            }
        }

        return bundleMessageDataContainerDto;
    }
}
