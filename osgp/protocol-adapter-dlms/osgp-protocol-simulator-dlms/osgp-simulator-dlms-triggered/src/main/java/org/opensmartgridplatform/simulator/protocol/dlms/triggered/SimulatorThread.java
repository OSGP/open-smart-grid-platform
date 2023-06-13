// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.triggered;

import java.io.IOException;
import org.openmuc.jdlms.DlmsServer;
import org.opensmartgridplatform.simulator.protocol.dlms.server.DeviceServer;
import org.opensmartgridplatform.simulator.protocol.dlms.triggered.utils.PortTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulatorThread extends Thread {

  private static final Object MONITOR = new Object();
  private final Long logicalId;
  private final Long port;
  private boolean started;
  private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorThread.class);
  private static final PortTracker PORT_TRACKER = new PortTracker();
  private static final Integer SIMULATOR_DELAY = 180000;
  private static final Integer STARTUP_CHECK_DELAY = 500;
  private static final Integer MAX_RETRIES = 20;

  protected SimulatorThread(final Long port, final Long logicalId) {
    this.port = port;
    this.logicalId = logicalId;
  }

  /**
   * Start running a thread that starts a device simulator, waits for three minutes and then closes
   * that simulator.
   */
  @Override
  public void run() {

    final String[] arguments = this.configureSimulatorArguments(this.port, this.logicalId);

    final DlmsServer dlmsServer = new DeviceServer().getDlmsServer(arguments);

    try {
      this.checkStartUp(this.port);
      Thread.sleep(SIMULATOR_DELAY);
    } catch (final InterruptedException e) {
      LOGGER.warn("SimulatorThread was interrupted.", e);
    }

    try {
      dlmsServer.close();
      if (this.unregisterPort(this.port)) {
        LOGGER.info("Successfully closed DlmsServer, port {} is free for future use.", this.port);
      }
    } catch (final IOException e) {
      LOGGER.warn("Failed to close DlmsServer on port {}", this.port, e);
    }
  }

  private boolean unregisterPort(final Long port) {
    return PORT_TRACKER.unregisterPort(port);
  }

  /**
   * For now, this method only configures the port and logicalId a deviceSimulator needs to start.
   * This is done intentionally, because for now nothing more is needed.
   *
   * <p>But this means that the deviceId that will be simulated by the device that is woken up,
   * almost never matches the device identification in the platform. As soon as more realistic
   * values will be expected, the number of items that are set based on the trigger call will need
   * to be expanded, and somehow used in the Cosem classes provided to the simulated device.
   */
  private String[] configureSimulatorArguments(final Long port, final Long logicalId) {

    return new String[] {
      "--deviceidentification.kemacode=ESIM14",
      "--deviceidentification.productionyear=00",
      "--deviceidentification.serialnumber=00000001",
      "--spring.profiles.active=default",
      "--logicalDeviceIds=" + logicalId,
      "--port=" + port
    };
  }

  private void checkStartUp(final Long port) throws InterruptedException {
    synchronized (MONITOR) {
      int i = 0;
      while (i < MAX_RETRIES) {
        MONITOR.wait(STARTUP_CHECK_DELAY);
        if (PORT_TRACKER.checkIfPortIsUsed(port)) {
          this.started = true;
          break;
        }
        i++;
      }
    }
  }

  public synchronized boolean isStarted() {
    return this.started;
  }
}
