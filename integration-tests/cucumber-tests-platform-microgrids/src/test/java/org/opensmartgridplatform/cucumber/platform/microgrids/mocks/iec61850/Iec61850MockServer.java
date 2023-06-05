// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.mocks.iec61850;

import com.beanit.openiec61850.SclParseException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.opensmartgridplatform.cucumber.core.RetryableAssert;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.RtuSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850MockServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850MockServer.class);

  private final String mockedDevice;

  private final String icdFilename;

  private final int port;

  private final String serverName;

  private RtuSimulator rtuSimulator;

  private boolean simulatorIsListening = false;

  public Iec61850MockServer(
      final String mockedDevice,
      final String icdFilename,
      final int port,
      final String serverName) {
    this.mockedDevice = mockedDevice;
    this.icdFilename = icdFilename;
    this.port = port;
    this.serverName = serverName;
  }

  public void start() {

    if (this.isInitialised()) {
      if (this.simulatorIsListening) {
        LOGGER.error("RtuSimulator for device {} was already started.", this.mockedDevice);
        throw new IllegalStateException("RtuSimulator was already started.");
      }
    } else {
      this.rtuSimulator = this.initialiseSimulator();
    }

    try {
      this.rtuSimulator.start();
      this.simulatorIsListening = true;
      LOGGER.info(
          "Started IEC61850 Mock server for device {} on port {}", this.mockedDevice, this.port);
    } catch (final IOException e) {
      LOGGER.error(
          "Expected IEC61850 Mock configuration allowing simulator for device {} startup.",
          this.mockedDevice);
      throw new AssertionError(
          "Expected IEC61850 Mock configuration allowing simulator startup.", e);
    }
  }

  public void stop() {

    if (this.rtuSimulator == null || !this.simulatorIsListening) {
      LOGGER.warn("Not stopping IEC61850 Mock server, because it was not running.");
      return;
    }

    this.rtuSimulator.stop();
    this.simulatorIsListening = false;
    LOGGER.info("Stopped IEC61850 Mock server for device {}", this.mockedDevice);
  }

  public void restart() {
    this.stop();
    RetryableAssert.assertWithRetries(this::start, 5, 500, TimeUnit.MILLISECONDS);
  }

  public void ensureReportsDisabled() {
    this.rtuSimulator.ensureReportsDisabled();
  }

  public void assertReportsEnabled() {
    this.rtuSimulator.assertAllReportsEnabled();
  }

  public void assertReportsDisabled() {
    this.rtuSimulator.assertNoReportsEnabled();
  }

  public void mockValue(final String logicalDeviceName, final String node, final String value) {
    if (!this.isInitialised()) {
      LOGGER.error("RtuSimulator for device {} has not yet been initialised.", this.mockedDevice);
      throw new AssertionError("RtuSimulator has not yet been initialised.");
    }
    this.rtuSimulator.mockValue(logicalDeviceName, node, value);
  }

  public void assertValue(final String logicalDeviceName, final String node, final String value) {
    if (!this.isInitialised()) {
      LOGGER.error("RtuSimulator for device {} has not yet been initialised.", this.mockedDevice);
      throw new AssertionError("RtuSimulator has not yet been initialised.");
    }
    this.rtuSimulator.assertValue(logicalDeviceName, node, value);
  }

  private boolean isInitialised() {
    return this.rtuSimulator != null;
  }

  private InputStream getIcdFile() {
    final InputStream icdFile = ClassLoader.getSystemResourceAsStream(this.icdFilename);
    if (icdFile == null) {
      throw new AssertionError("Expected IEC61850 Mock ICD file: " + this.icdFilename);
    }
    return icdFile;
  }

  private RtuSimulator initialiseSimulator() {
    try {
      return new RtuSimulator(this.port, this.getIcdFile(), this.serverName);
    } catch (final SclParseException e) {
      LOGGER.error(
          "Expected IEC61850 Mock configuration allowing simulator for device {} startup.",
          this.mockedDevice);
      throw new AssertionError(
          "Expected IEC61850 Mock configuration allowing simulator startup.", e);
    }
  }
}
