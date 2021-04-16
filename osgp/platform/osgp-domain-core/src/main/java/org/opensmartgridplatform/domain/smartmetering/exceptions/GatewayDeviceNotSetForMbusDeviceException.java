/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.smartmetering.exceptions;

import org.opensmartgridplatform.domain.core.exceptions.PlatformException;

public class GatewayDeviceNotSetForMbusDeviceException extends PlatformException {

  private static final long serialVersionUID = 1309508748012622409L;

  private static final String MESSAGE =
      "Meter for gas reads should have an energy meter as gateway device.";

  public GatewayDeviceNotSetForMbusDeviceException() {
    super(MESSAGE);
  }
}
