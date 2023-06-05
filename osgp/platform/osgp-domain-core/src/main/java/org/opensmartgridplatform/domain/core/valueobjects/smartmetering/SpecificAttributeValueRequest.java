// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public class SpecificAttributeValueRequest extends SpecificAttributeValueRequestData {

  private static final long serialVersionUID = 1;

  private final String deviceIdentification;

  public SpecificAttributeValueRequest(
      final int classId,
      final int attribute,
      final ObisCodeValues obisCode,
      final String deviceIdentification) {
    super(classId, attribute, obisCode);
    this.deviceIdentification = deviceIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime * result
            + ((this.deviceIdentification == null) ? 0 : this.deviceIdentification.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SpecificAttributeValueRequest other = (SpecificAttributeValueRequest) obj;
    if (this.deviceIdentification == null) {
      if (other.deviceIdentification != null) {
        return false;
      }
    } else if (!this.deviceIdentification.equals(other.deviceIdentification)) {
      return false;
    }
    return true;
  }
}
