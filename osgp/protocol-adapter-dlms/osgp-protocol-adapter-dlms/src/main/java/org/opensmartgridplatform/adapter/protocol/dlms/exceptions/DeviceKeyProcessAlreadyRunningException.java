/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import java.time.Duration;

public class DeviceKeyProcessAlreadyRunningException extends DirectlyRetryableException {

  private static final long serialVersionUID = 1998438538193678335L;

  public DeviceKeyProcessAlreadyRunningException(final Duration delay) {
    super(delay);
  }
}
