/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public interface ActionRequest extends Serializable {
  /**
   * Validates the ActionRequest.
   *
   * @throws FunctionalException is thrown when the validation is not ok.
   */
  void validate() throws FunctionalException;

  /**
   * @returns the appropriate {@link DeviceFunction} for the object
   */
  DeviceFunction getDeviceFunction();
}
