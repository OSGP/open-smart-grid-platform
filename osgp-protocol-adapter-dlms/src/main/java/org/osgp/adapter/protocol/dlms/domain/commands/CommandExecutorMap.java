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

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;

@Component
public class CommandExecutorMap {

    private final Map<Class<? extends ActionRequestDto>, CommandExecutor<? extends ActionRequestDto, ? extends ActionResponseDto>> commandExecutors = new HashMap<>();

    public void addCommandExecutor(final Class<? extends ActionRequestDto> clazz,
            final CommandExecutor<? extends ActionRequestDto, ? extends ActionResponseDto> commandExecutor) {

        this.commandExecutors.put(clazz, commandExecutor);
    }

    @SuppressWarnings("unchecked")
    public CommandExecutor<ActionRequestDto, ActionResponseDto> getCommandExecutor(
            final Class<? extends ActionRequestDto> clazz) {
        return (CommandExecutor<ActionRequestDto, ActionResponseDto>) this.commandExecutors.get(clazz);
    }
}
