// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.smartmetering.exceptions;

import org.opensmartgridplatform.domain.core.exceptions.PlatformException;

public class GatewayDeviceInvalidForMbusDeviceException extends PlatformException {

  private static final long serialVersionUID = 1309508748012622409L;

  private static final String MESSAGE = "The M-Bus device is connected to another gateway device.";

  public GatewayDeviceInvalidForMbusDeviceException() {
    super(MESSAGE);
  }
}
