/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;

@Component
public class CommandExecutorMap {

    private final Map<Class<? extends ActionDto>, CommandExecutor<? extends ActionDto, ? extends ActionResponseDto>> commandExecutors = new HashMap<>();

    public void addCommandExecutor(final Class<? extends ActionDto> clazz,
            final CommandExecutor<? extends ActionDto, ? extends ActionResponseDto> commandExecutor) {

        this.commandExecutors.put(clazz, commandExecutor);
    }

    @SuppressWarnings("unchecked")
    public CommandExecutor<ActionDto, ActionResponseDto> getCommandExecutor(final Class<? extends ActionDto> clazz) {
        return (CommandExecutor<ActionDto, ActionResponseDto>) this.commandExecutors.get(clazz);
    }
}
