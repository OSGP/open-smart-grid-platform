// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionParameters;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;

public interface Client {

  /**
   * Connect to a device.
   *
   * @param connectionParameters The {@link ConnectionParameters} instance.
   * @param asduListener The {@link ConnectionEventListener} instance.
   * @return A {@link ClientConnection} instance.
   * @throws ConnectionFailureException
   */
  ClientConnection connect(
      ConnectionParameters connectionParameters, ConnectionEventListener asduListener)
      throws ConnectionFailureException;

  /**
   * Disconnect from the device.
   *
   * @param clientConnection The {@link ClientConnection} instance.
   */
  void disconnect(ClientConnection clientConnection);
}
