/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.util.List;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.CommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.CommandExecutorMap;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessagesRequestDto;

@Service(value = "dlmsBundleService")
public class BundleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

    @Autowired
    private CommandExecutorMap bundleCommandExecutorMap;

    public BundleMessagesRequestDto callExecutors(final DlmsConnection conn, final DlmsDevice device,
            final BundleMessagesRequestDto bundleMessagesRequest) {

        final List<ActionDto> actionList = bundleMessagesRequest.getActionList();
        for (final ActionDto actionDto : actionList) {

            // Only execute the request when there is no response available yet.
            // Because it could be a retry.
            if (actionDto.getResponse() == null) {

                final CommandExecutor<ActionRequestDto, ActionResponseDto> executor = this.bundleCommandExecutorMap
                        .getCommandExecutor(actionDto.getRequest().getClass());

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

                    final String strexec = (executor == null) ? " = null " : executor.getClass().getSimpleName();

                    LOGGER.error("Error while executing bundle action "
                            + actionDto.getRequest().getClass().getSimpleName() + " for class "
                            + actionDto.getRequest().getClass().getSimpleName() + " and executor " + strexec, exception);
                    actionDto.setResponse(new ActionResponseDto(exception,
                            "Error while executing bundle action for class "
                                    + actionDto.getRequest().getClass().getSimpleName() + " and executor " + strexec));
                }
            }
        }

        return bundleMessagesRequest;
    }
}
