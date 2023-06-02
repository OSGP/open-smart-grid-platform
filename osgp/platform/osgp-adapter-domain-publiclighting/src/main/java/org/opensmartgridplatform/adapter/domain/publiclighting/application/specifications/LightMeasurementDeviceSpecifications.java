//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.application.specifications;

import java.time.Instant;
import javax.validation.constraints.NotNull;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.springframework.data.jpa.domain.Specification;

public class LightMeasurementDeviceSpecifications
    extends DeviceSpecifications<LightMeasurementDevice> {

  public final Specification<LightMeasurementDevice> hasLastCommunicationTimeBefore(
      @NotNull final Instant instant) {
    return (root, query, cb) -> cb.lessThan(root.<Instant>get("lastCommunicationTime"), instant);
  }
}
