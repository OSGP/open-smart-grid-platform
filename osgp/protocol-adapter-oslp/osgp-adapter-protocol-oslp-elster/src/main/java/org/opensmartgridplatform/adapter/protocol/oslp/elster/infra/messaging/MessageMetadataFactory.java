/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public class MessageMetadataFactory {
  public static MessageMetadata from(
      final DeviceResponse deviceResponse, final String messageType) {
    return new MessageMetadata.Builder()
        .withDeviceIdentification(deviceResponse.getDeviceIdentification())
        .withOrganisationIdentification(deviceResponse.getOrganisationIdentification())
        .withCorrelationUid(deviceResponse.getCorrelationUid())
        .withMessageType(messageType)
        .withMessagePriority(deviceResponse.getMessagePriority())
        .build();
  }

  private MessageMetadataFactory() {
    // empty constructor
  }
}
