/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.TransitionMessageDataContainerDto;

public class SetTransitionDeviceRequest extends DeviceRequest {

  private final TransitionMessageDataContainerDto transitionTypeContainer;

  public SetTransitionDeviceRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final TransitionMessageDataContainerDto transitionTypeContainer) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    this.transitionTypeContainer = transitionTypeContainer;
  }

  public SetTransitionDeviceRequest(
      final Builder deviceRequestBuilder,
      final TransitionMessageDataContainerDto transitionTypeContainer) {
    super(deviceRequestBuilder);
    this.transitionTypeContainer = transitionTypeContainer;
  }

  public TransitionMessageDataContainerDto getTransitionTypeContainer() {
    return this.transitionTypeContainer;
  }
}
