// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.triggered;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulatorThreadStarter {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorThreadStarter.class);
  private static final Integer MAX_RETRIES = 20;
  private static final Integer MILLISECONDS_DELAY = 500;

  /**
   * Starts a new thread. This new thread will start an instance of DlmsServer, using the provided
   * arguments.
   *
   * @param port the port to start a simulator on
   * @param logicalId the logicalId of the logicalDevice in the simulator
   */
  public void startSimulatorThread(final Long port, final Long logicalId) {

    LOGGER.info("Starting SimulatorThread..");
    final SimulatorThread simulatorThread = new SimulatorThread(port, logicalId);

    simulatorThread.start();

    try {
      this.waitForSimulatorStartUp(simulatorThread);
    } catch (final InterruptedException e) {
      LOGGER.warn("Waiting for startup was interrupted or reached he maximum number of retries", e);
    }
  }

  private void waitForSimulatorStartUp(final SimulatorThread simulatorThread)
      throws InterruptedException {
    for (int i = 0; i < MAX_RETRIES; i++) {
      Thread.sleep(MILLISECONDS_DELAY);
      if (simulatorThread.isStarted()) {
        break;
      }
    }
  }
}
