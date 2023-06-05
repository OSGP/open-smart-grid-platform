// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.springframework.stereotype.Component;

@Component
public class CommandExecutorMap<T, R> {

  private final Map<Class<? extends ActionRequestDto>, CommandExecutor<T, R>> commandExecutors =
      new HashMap<>();

  public void addCommandExecutor(
      final Class<? extends ActionRequestDto> clazz, final CommandExecutor<T, R> commandExecutor) {

    this.commandExecutors.put(clazz, commandExecutor);
  }

  public CommandExecutor<T, R> getCommandExecutor(final Class<? extends ActionRequestDto> clazz) {
    return this.commandExecutors.get(clazz);
  }
}
