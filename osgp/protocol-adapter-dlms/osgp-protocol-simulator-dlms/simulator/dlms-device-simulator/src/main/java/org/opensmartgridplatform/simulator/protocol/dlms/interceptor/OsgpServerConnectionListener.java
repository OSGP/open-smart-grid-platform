// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.interceptor;

import java.security.SecureRandom;
import org.openmuc.jdlms.ServerConnectionInfo;
import org.openmuc.jdlms.ServerConnectionInfo.Status;
import org.openmuc.jdlms.ServerConnectionListener;
import org.opensmartgridplatform.simulator.protocol.dlms.exception.SimulatorRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgpServerConnectionListener implements ServerConnectionListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(OsgpServerConnectionListener.class);

  private SecureRandom random = new SecureRandom();

  private final int connectionSetupDelayMin;
  private final int connectionSetupDelayMax;

  public OsgpServerConnectionListener(
      final int connectionSetupDelayMin, final int connectionSetupDelayMax) {
    this.connectionSetupDelayMin = connectionSetupDelayMin;
    this.connectionSetupDelayMax = connectionSetupDelayMax;
  }

  public OsgpServerConnectionListener(final int delay) {
    this(delay, delay);
  }

  @Override
  public void connectionChanged(final ServerConnectionInfo connectionInfo) {

    LOGGER.debug("Connection changed to {}", connectionInfo.getConnectionStatus());
    LOGGER.debug(
        "Connection info client address is {}",
        connectionInfo.getClientInetAddress().getHostAddress());

    if (Status.OPEN.equals(connectionInfo.getConnectionStatus())) {
      try {
        final int randomSleep = this.getRandomSleep();
        LOGGER.debug(
            "Delaying connection setup for {} milisec. Logical device address: {}",
            randomSleep,
            connectionInfo.getLogicalDeviceAddress());
        Thread.sleep(randomSleep);
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new SimulatorRuntimeException(e);
      }
    }
  }

  private int getRandomSleep() {
    if (this.connectionSetupDelayMin == this.connectionSetupDelayMax) {
      return this.connectionSetupDelayMax;
    }
    return this.random.nextInt(this.connectionSetupDelayMax - this.connectionSetupDelayMin)
        + this.connectionSetupDelayMin;
  }
}
