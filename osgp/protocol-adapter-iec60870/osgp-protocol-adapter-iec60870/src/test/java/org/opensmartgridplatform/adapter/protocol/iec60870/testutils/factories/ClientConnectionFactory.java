// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
