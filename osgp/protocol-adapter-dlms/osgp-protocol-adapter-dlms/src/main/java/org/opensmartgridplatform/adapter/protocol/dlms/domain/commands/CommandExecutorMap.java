/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.springframework.stereotype.Component;

@Component
public class CommandExecutorMap {

  private final Map<Class<? extends ActionRequestDto>, CommandExecutor<?, ?>> commandExecutors =
      new HashMap<>();

  public void addCommandExecutor(
      final Class<? extends ActionRequestDto> clazz, final CommandExecutor<?, ?> commandExecutor) {

    this.commandExecutors.put(clazz, commandExecutor);
  }

  public CommandExecutor<?, ?> getCommandExecutor(final Class<? extends ActionRequestDto> clazz) {
    return this.commandExecutors.get(clazz);
  }
}
