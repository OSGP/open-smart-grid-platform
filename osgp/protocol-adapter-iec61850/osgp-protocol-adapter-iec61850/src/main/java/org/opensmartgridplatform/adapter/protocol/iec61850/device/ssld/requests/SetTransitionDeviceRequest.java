/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.TransitionMessageDataContainerDto;

public class SetTransitionDeviceRequest extends DeviceRequest {

  private TransitionMessageDataContainerDto transitionTypeContainer;

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
