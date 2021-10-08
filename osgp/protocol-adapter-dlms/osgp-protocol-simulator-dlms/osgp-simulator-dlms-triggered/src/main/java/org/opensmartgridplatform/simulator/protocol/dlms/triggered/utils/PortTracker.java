/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.triggered.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortTracker {

  private static final Logger LOGGER = LoggerFactory.getLogger(PortTracker.class);

  private static final Set<Long> USED_PORTS = new HashSet<>();

  /**
   * Method to make sure a port is free before attempting to start a device simulator on that
   * specific port.
   *
   * @param port the port number to be checked.
   * @return true if the port is already in use, either by a device simulator or something else,
   *     false if the port is free for use.
   */
  public synchronized boolean isPortUsed(final Long port) {

    if (this.portIsRegistered(port)) {
      return true;
    } else {
      return this.checkIfPortIsUsed(port);
    }
  }

  /**
   * Method to check whether or not a port number is already registered to usedPorts.
   *
   * @param port the port number to be checked.
   * @return false if a port is unused, true if a port is used.
   */
  public synchronized boolean portIsRegistered(final Long port) {

    if (USED_PORTS.contains(port)) {
      return true;
    } else {
      this.registerPort(port);
      return false;
    }
  }

  /**
   * Method to check if a port is not already in use by another application. If the port is already
   * in use, it is registered to usedPorts.
   *
   * @param port the port number to be checked for availability.
   * @return true if the port is used, false if the port is free for use.
   */
  public synchronized boolean checkIfPortIsUsed(final Long port) {

    try {
      // Try to open a LOCAL port
      new ServerSocket(port.intValue()).close();
      // local port can be opened, it's not used
      return false;
    } catch (final IOException e) {
      LOGGER.debug("Unable to open/close port " + port + ", registering it as in use.", e);
      this.registerPort(port);
      // local port cannot be opened, it's in use
      return true;
    }
  }

  /**
   * Once a device simulator is shut down, the port it was using becomes free for future use, so it
   * should be unregistered from usedPorts.
   *
   * @param port the port number to be unregistered.
   * @return true if unregistering was successful, false in case of an error.
   */
  public synchronized boolean unregisterPort(final Long port) {
    return USED_PORTS.remove(port);
  }

  /**
   * Method to register port to usedPorts, to enable keeping track of ports that are or are not free
   * for starting up a device simulator
   *
   * @param port the port number to be registered as used.
   */
  private synchronized void registerPort(final Long port) {
    USED_PORTS.add(port);
  }
}
