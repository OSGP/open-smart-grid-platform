// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class DefinableLoadProfileConfigurationData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -2735837696949265144L;

  private final List<CaptureObjectDefinition> captureObjects = new ArrayList<>();
  private final Long capturePeriod;

  public DefinableLoadProfileConfigurationData(
      final List<CaptureObjectDefinition> captureObjects, final Long capturePeriod) {
    if (captureObjects != null) {
      this.captureObjects.addAll(captureObjects);
    }
    this.capturePeriod = capturePeriod;
  }

  public boolean hasCaptureObjects() {
    return !this.captureObjects.isEmpty();
  }

  public List<CaptureObjectDefinition> getCaptureObjects() {
    return new ArrayList<>(this.captureObjects);
  }

  public boolean hasCapturePeriod() {
    return this.capturePeriod != null;
  }

  public Long getCapturePeriod() {
    return this.capturePeriod;
  }

  @Override
  public void validate() throws FunctionalException {
    if (!(this.hasCaptureObjects() || this.hasCapturePeriod())) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              "Some attribute has to be given a non-null value configuring the definable load profile."));
    }
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.CONFIGURE_DEFINABLE_LOAD_PROFILE;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("DefinableLoadProfile[");
    if (this.hasCaptureObjects()) {
      sb.append("captureObjects=").append(this.captureObjects);
    }
    if (this.hasCapturePeriod()) {
      sb.append("capturePeriod=").append(this.capturePeriod);
    }
    return sb.append(']').toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.captureObjects, this.capturePeriod);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DefinableLoadProfileConfigurationData)) {
      return false;
    }
    final DefinableLoadProfileConfigurationData other = (DefinableLoadProfileConfigurationData) obj;
    return Objects.equals(this.captureObjects, other.captureObjects)
        && Objects.equals(this.capturePeriod, other.capturePeriod);
  }
}
