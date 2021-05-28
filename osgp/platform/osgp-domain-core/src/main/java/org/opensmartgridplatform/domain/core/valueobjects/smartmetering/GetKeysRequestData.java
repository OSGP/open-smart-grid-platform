/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.util.List;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class GetKeysRequestData implements ActionRequest {

  private static final long serialVersionUID = -1601425472295052943L;
  private final List<SecretType> secretTypes;

  public GetKeysRequestData(final List<SecretType> secretTypes) {
    this.secretTypes = secretTypes;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.GET_KEYS;
  }

  public List<SecretType> getSecretTypes() {
    return this.secretTypes;
  }
}
