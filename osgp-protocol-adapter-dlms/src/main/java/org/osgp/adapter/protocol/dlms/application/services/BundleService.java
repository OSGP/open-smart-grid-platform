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

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.BundleCommandExecutorMap;
import org.osgp.adapter.protocol.dlms.domain.commands.CommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessageDataContainerDto;

@Service(value = "dlmsBundleService")
public class BundleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

    @Autowired
    private BundleCommandExecutorMap bundleCommandExecutorMap;

    public List<ActionResponseDto> callExecutors(final ClientConnection conn, final DlmsDevice device,
            final BundleMessageDataContainerDto bundleMessageDataContainerDto) {
        final List<ActionResponseDto> actionValueObjectResponseDtoList = new ArrayList<>();

        for (final ActionDto actionValueObjectDto : bundleMessageDataContainerDto.getActionList()) {

            final CommandExecutor<ActionDto, ActionResponseDto> executor = this.bundleCommandExecutorMap
                    .getBundleCommandExecutor(actionValueObjectDto.getClass());

            try {
                LOGGER.info("Calling executor in bundle {}", executor.getClass());
                final ActionResponseDto actionResult = executor.execute(conn, device, actionValueObjectDto);
                actionValueObjectResponseDtoList.add(actionResult);
            } catch (final Exception e) {
                LOGGER.error("Error while executing bundle action for class " + actionValueObjectDto.getClass()
                        + " and executor " + executor.getClass(), e);
                final ActionResponseDto actionValueObjectResponseDto = new ActionResponseDto(e,
                        "Error while executing bundle action for class " + actionValueObjectDto.getClass()
                        + " and executor " + executor.getClass());
                actionValueObjectResponseDtoList.add(actionValueObjectResponseDto);
            }
        }

        return actionValueObjectResponseDtoList;
    }
}
