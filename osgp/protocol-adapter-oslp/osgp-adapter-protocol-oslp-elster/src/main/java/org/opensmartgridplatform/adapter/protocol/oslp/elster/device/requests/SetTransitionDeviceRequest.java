// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
