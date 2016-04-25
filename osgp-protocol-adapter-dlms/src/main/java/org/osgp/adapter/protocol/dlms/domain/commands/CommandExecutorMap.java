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
