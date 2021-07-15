/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories;

import org.mockito.Mockito;
import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;

public class ClientConnectionFactory {

  public static ClientConnection forDevice(final String deviceIdentification) {
    final Connection connection = Mockito.mock(Connection.class);
    final ConnectionParameters connectionParameters =
        new ConnectionParameters.Builder().deviceIdentification(deviceIdentification).build();
    return new DeviceConnection(connection, connectionParameters);
  }
}
