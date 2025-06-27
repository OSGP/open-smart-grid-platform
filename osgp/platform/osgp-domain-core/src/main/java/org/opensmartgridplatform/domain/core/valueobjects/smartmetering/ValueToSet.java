// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ValueToSet implements Serializable {

  private static final long serialVersionUID = 3413354995054664052L;

  private final String objectType;
  private final int attribute;
  private final int intValue;

  public ValueToSet(final String objectType, final int attribute, final int intValue) {
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
    final ValueToSet other = (ValueToSet) obj;
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
}
