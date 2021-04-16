/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
