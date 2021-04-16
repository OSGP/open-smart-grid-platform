/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.specifications;

import javax.validation.constraints.NotNull;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.data.jpa.domain.Specification;

public class DeviceSpecifications<D extends Device> {

  public final Specification<D> hasDeviceIdentification(
      @NotNull final String deviceIdentification) {
    return (root, query, cb) ->
        cb.equal(root.<String>get("deviceIdentification"), deviceIdentification);
  }

  public final Specification<D> hasDeviceType(@NotNull final String deviceType) {
    return (root, query, cb) -> cb.equal(root.<String>get("deviceType"), deviceType);
  }

  public final Specification<D> hasDeviceLifeCycleStatus(
      @NotNull final DeviceLifecycleStatus deviceLifecycleStatus) {
    return (root, query, cb) ->
        cb.equal(root.<DeviceLifecycleStatus>get("deviceLifecycleStatus"), deviceLifecycleStatus);
  }

  public final Specification<D> withoutGateway() {
    return (root, query, cb) -> cb.isNull(root.<Device>get("gatewayDevice"));
  }
}
