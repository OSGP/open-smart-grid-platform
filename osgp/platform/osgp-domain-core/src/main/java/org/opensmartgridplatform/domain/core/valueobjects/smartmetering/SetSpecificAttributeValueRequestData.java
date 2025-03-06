// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetSpecificAttributeValueRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -7326169764207317011L;

  private final String objectType;
  private final int attribute;
  private final int intValue;

  public SetSpecificAttributeValueRequestData(
      final String objectType, final int attribute, final int intValue) {
    super();
    this.objectType = objectType;
    this.attribute = attribute;
    this.intValue = intValue;
  }

  public String getObjectType() {
    return this.objectType;
  }

  public int getAttribute() {
    return this.attribute;
  }

  public int getIntValue() {
    return this.intValue;
  }

  @Override
  public void validate() throws FunctionalException {
    // not needed here
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.attribute;
    result = (prime * result) + this.intValue;
    result = (prime * result) + this.objectType.hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SetSpecificAttributeValueRequestData other = (SetSpecificAttributeValueRequestData) obj;
    if (this.attribute != other.attribute) {
      return false;
    }
    if (this.intValue != other.intValue) {
      return false;
    }
    if (this.objectType == null) {
      if (other.objectType != null) {
        return false;
      }
    } else if (!this.objectType.equals(other.objectType)) {
      return false;
    }
    return true;
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_SPECIFIC_ATTRIBUTE_VALUE;
  }
}
