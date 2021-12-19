/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Iec61850SetConfigurationFunction;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;

public class Iec61850SetConfigurationCommand {

  private final DeviceMessageLoggingService loggingService;

  public Iec61850SetConfigurationCommand(final DeviceMessageLoggingService loggingService) {
    this.loggingService = loggingService;
  }

  public void setConfigurationOnDevice(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final ConfigurationDto configuration)
      throws ProtocolAdapterException {
    final Function<Void> function =
        new Iec61850SetConfigurationFunction(
            iec61850Client, deviceConnection, configuration, this.loggingService);

    iec61850Client.sendCommandWithRetry(
        function, "SetConfiguration", deviceConnection.getDeviceIdentification());
  }
}
