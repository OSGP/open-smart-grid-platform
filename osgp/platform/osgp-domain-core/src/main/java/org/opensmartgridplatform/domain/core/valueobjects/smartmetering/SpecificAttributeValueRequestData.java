// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SpecificAttributeValueRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = 2732162650167261497L;

  private final int classId;
  private final int attribute;
  private final ObisCodeValues obisCode;

  public SpecificAttributeValueRequestData(
      final int classId, final int attribute, final ObisCodeValues obisCode) {
    super();
    this.classId = classId;
    this.attribute = attribute;
    this.obisCode = obisCode;
  }

  public int getClassId() {
    return this.classId;
  }

  public int getAttribute() {
    return this.attribute;
  }

  public ObisCodeValues getObisCode() {
    return this.obisCode;
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
    result = (prime * result) + this.classId;
    result = (prime * result) + ((this.obisCode == null) ? 0 : this.obisCode.hashCode());
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
    final SpecificAttributeValueRequestData other = (SpecificAttributeValueRequestData) obj;
    if (this.attribute != other.attribute) {
      return false;
    }
    if (this.classId != other.classId) {
      return false;
    }
    if (this.obisCode == null) {
      if (other.obisCode != null) {
        return false;
      }
    } else if (!this.obisCode.equals(other.obisCode)) {
      return false;
    }
    return true;
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.GET_SPECIFIC_ATTRIBUTE_VALUE;
  }
}
