// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
