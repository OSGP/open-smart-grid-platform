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

import javax.annotation.PostConstruct;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.CommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsBundleGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetAdministrativeStatusBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsGasBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReadAlarmRegisterCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveEventsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetSpecialDaysBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataGasDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;

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
    private ReadAlarmRegisterCommandExecutor readAlarmRegisterCommandExecutor;

    @Autowired
    private GetAdministrativeStatusBundleCommandExecutor getAdministrativeStatusBundleCommandExecutor;

    private final static Map<Class<? extends ActionValueObjectDto>, CommandExecutor<? extends ActionValueObjectDto, ? extends ActionValueObjectResponseDto>> CLAZZ_EXECUTOR_MAP = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        CLAZZ_EXECUTOR_MAP.put(FindEventsQueryDto.class, this.retrieveEventsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(ActualMeterReadsDataDto.class, this.actualMeterReadsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(ActualMeterReadsDataGasDto.class, this.actualMeterReadsBundleGasCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(SpecialDaysRequestDataDto.class, this.setSpecialDaysBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(ReadAlarmRegisterRequestDto.class, this.readAlarmRegisterCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(GetAdministrativeStatusDataDto.class, this.getAdministrativeStatusBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(PeriodicMeterReadsRequestDataDto.class, this.getPeriodicMeterReadsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(PeriodicMeterReadsGasRequestDataDto.class,
                this.getPeriodicMeterReadsGasBundleCommandExecutor);
    }

    public List<ActionValueObjectResponseDto> callExecutors(final ClientConnection conn, final DlmsDevice device,
            final BundleMessageDataContainerDto bundleMessageDataContainerDto) {
        final List<ActionValueObjectResponseDto> actionValueObjectResponseDtoList = new ArrayList<>();

        for (final ActionValueObjectDto actionValueObjectDto : bundleMessageDataContainerDto.getFindEventsQueryList()) {

            // suppress else the compiler will complain
            @SuppressWarnings({ "unchecked" })
            final CommandExecutor<ActionValueObjectDto, ActionValueObjectResponseDto> executor = (CommandExecutor<ActionValueObjectDto, ActionValueObjectResponseDto>) CLAZZ_EXECUTOR_MAP
                    .get(actionValueObjectDto.getClass());

            try {

                final ActionValueObjectResponseDto actionResult = executor.execute(conn, device, actionValueObjectDto);
                actionValueObjectResponseDtoList.add(actionResult);
            } catch (final Exception e) {
                final ActionValueObjectResponseDto actionValueObjectResponseDto = new ActionValueObjectResponseDto(e,
                        "Error while executing bundle action for class " + actionValueObjectDto.getClass()
                                + " and executor " + executor.getClass());
                actionValueObjectResponseDtoList.add(actionValueObjectResponseDto);
            }
        }

        return actionValueObjectResponseDtoList;
    }
}
