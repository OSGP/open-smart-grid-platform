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
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveEventsBundleCommandExecutor;
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

@Service(value = "dlmsBundleService")
public class BundleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

    @Autowired
    private RetrieveEventsBundleCommandExecutor retrieveEventsBundleCommandExecutor;

    @Autowired
    private GetPeriodicMeterReadsCommandExecutor getPeriodicMeterReadsCommandExecutor;

    @Autowired
    private GetActualMeterReadsBundleCommandExecutor actualMeterReadsBundleCommandExecutor;

    @Autowired
    private GetActualMeterReadsGasCommandExecutor actualMeterReadsGasCommandExecutor;

    @Autowired
    private GetPeriodicMeterReadsGasCommandExecutor getPeriodicMeterReadsGasCommandExecutor;

    private final static Map<Class<? extends ActionValueObjectDto>, CommandExecutor<? extends ActionValueObjectDto, ? extends ActionValueObjectResponseDto>> CLAZZ_EXECUTOR_MAP = new HashMap<>();

    // ? extends CommandExecutor<? extends ActionValueObjectDto, Object>
    // ? extends CommandExecutor<?,?>

    @PostConstruct
    private void postConstruct() {
        CLAZZ_EXECUTOR_MAP.put(FindEventsQueryDto.class, this.retrieveEventsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(ActualMeterReadsDataDto.class, this.actualMeterReadsBundleCommandExecutor);
        CLAZZ_EXECUTOR_MAP.put(ActualMeterReadsDataGasDto.class, this.actualMeterReadsGasCommandExecutor);
    }

    public List<ActionValueObjectResponseDto> callExecutors(final ClientConnection conn, final DlmsDevice device,
            final BundleMessageDataContainerDto bundleMessageDataContainerDto) {
        final List<ActionValueObjectResponseDto> actionValueObjectResponseDtoList = new ArrayList<>();

        for (final ActionValueObjectDto actionValueObjectDto : bundleMessageDataContainerDto.getFindEventsQueryList()) {

            // suppress else the compiler will complain
            @SuppressWarnings({ "unchecked", "rawtypes" })
            final CommandExecutor<ActionValueObjectDto, ActionValueObjectResponseDto> executor = (CommandExecutor) CLAZZ_EXECUTOR_MAP
            .get(actionValueObjectDto.getClass());

            try {

                final ActionValueObjectResponseDto actionResult = executor.execute(conn, device, actionValueObjectDto);
                actionValueObjectResponseDtoList.add(actionResult);
            } catch (final Exception e) {
                final ActionValueObjectResponseDto actionValueObjectResponseDto = new ActionValueObjectResponseDto();
                actionValueObjectResponseDto.setException(e);
                actionValueObjectResponseDto.setResultString("Error while executing bundle action for class "
                        + actionValueObjectDto.getClass() + " and executor " + executor.getClass());
                actionValueObjectResponseDtoList.add(actionValueObjectResponseDto);
            }
        }

        return actionValueObjectResponseDtoList;
    }
}
