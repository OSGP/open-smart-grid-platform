/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class GetMbusEncryptionKeyStatusRequestData extends MbusActionRequest {

  private static final long serialVersionUID = 3636769765482239443L;

  public GetMbusEncryptionKeyStatusRequestData(final String mbusDeviceIdentification) {
    super(mbusDeviceIdentification);
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed

  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS;
  }
}
