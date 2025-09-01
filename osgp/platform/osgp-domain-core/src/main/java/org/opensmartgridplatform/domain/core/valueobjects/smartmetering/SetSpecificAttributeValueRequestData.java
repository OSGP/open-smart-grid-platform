// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetSpecificAttributeValueRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -7326169764207317011L;

  private final List<ValueToSet> valuesToSet;

  public SetSpecificAttributeValueRequestData(final List<ValueToSet> valuesToSet) {
    super();
    this.valuesToSet = valuesToSet;
  }

  public List<ValueToSet> getValuesToSet() {
    return this.valuesToSet;
  }

  @Override
  public void validate() throws FunctionalException {
    // not needed here
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    for (final ValueToSet valueToSet : this.valuesToSet) {
      result = (prime * result) + valueToSet.hashCode();
    }
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
    for (int i = 0; i < this.valuesToSet.size(); i++) {
      if (!this.valuesToSet.get(i).equals(other.valuesToSet.get(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_SPECIFIC_ATTRIBUTE_VALUE;
  }
}
