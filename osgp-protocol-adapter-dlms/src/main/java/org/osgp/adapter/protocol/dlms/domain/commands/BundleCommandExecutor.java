/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
