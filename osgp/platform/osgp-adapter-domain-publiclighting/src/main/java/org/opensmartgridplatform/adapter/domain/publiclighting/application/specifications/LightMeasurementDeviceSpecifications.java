/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
