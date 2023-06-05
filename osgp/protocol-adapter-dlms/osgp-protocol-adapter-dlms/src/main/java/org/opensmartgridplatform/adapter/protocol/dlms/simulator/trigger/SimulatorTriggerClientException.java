// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.simulator.trigger;

import org.opensmartgridplatform.shared.usermanagement.WebClientException;

public class SimulatorTriggerClientException extends WebClientException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 8612210328198618111L;

  public SimulatorTriggerClientException(final String message) {
    super(message);
  }

  public SimulatorTriggerClientException(final String message, final Throwable t) {
    super(message, t);
  }
}
