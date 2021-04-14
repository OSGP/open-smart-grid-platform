/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetEncryptionKeyExchangeOnGMeterRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = 3965412208032103531L;

  private final String deviceIdentification;

  public SetEncryptionKeyExchangeOnGMeterRequestData(final String deviceIdentification) {
    super();
    this.deviceIdentification = deviceIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER;
  }
}
