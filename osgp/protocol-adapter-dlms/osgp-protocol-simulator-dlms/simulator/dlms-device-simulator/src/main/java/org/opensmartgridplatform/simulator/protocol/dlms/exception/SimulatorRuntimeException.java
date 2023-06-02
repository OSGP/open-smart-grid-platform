//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.exception;

public class SimulatorRuntimeException extends RuntimeException {
  /** Serial Version UID. */
  private static final long serialVersionUID = 3151698143549959719L;

  public SimulatorRuntimeException(final String message) {
    super(message);
  }

  public SimulatorRuntimeException(final String message, final Throwable throwable) {
    super(message, throwable);
  }

  public SimulatorRuntimeException(final InterruptedException e) {
    super(e);
  }
}
