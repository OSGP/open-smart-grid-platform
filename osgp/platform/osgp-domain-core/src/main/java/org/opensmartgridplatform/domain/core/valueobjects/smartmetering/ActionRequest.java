// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
