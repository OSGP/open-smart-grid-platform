//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
