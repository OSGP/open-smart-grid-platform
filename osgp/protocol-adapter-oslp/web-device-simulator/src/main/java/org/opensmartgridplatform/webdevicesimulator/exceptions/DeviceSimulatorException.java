// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.exceptions;

public class DeviceSimulatorException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -7903758769242215843L;

  public DeviceSimulatorException(final String message) {
    super(message);
  }

  public DeviceSimulatorException(final String message, final Exception e) {
    super(message, e);
  }
}
