package org.osgp.adapter.protocol.dlms.domain.commands;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;

public abstract class BundleCommandExecutor<T extends ActionDto, R extends ActionResponseDto> implements
CommandExecutor<T, R> {

    @Autowired
    private CommandExecutorMap bundleCommandExecutorMap;

    private Class<T> actionDtoType;

    public BundleCommandExecutor(final Class<T> actionDtoType) {
        this.actionDtoType = actionDtoType;
    }

    @PostConstruct
    public void init() {
        this.bundleCommandExecutorMap.addCommandExecutor(this.actionDtoType, this);
    }
}
