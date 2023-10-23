/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services.utils;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata.Builder;

public class MessageMetadataUtil {

  private MessageMetadataUtil() {
    // Static class
  }

  public static MessageMetadata buildMetadata(
      final MessageMetadata messageMetadata, final Device device) {

    final Builder builder = messageMetadata.builder();
    builder.withDeviceIdentification(device.getDeviceIdentification());
    builder.withNetworkAddress(device.getNetworkAddress());
    builder.withNetworkSegmentIds(device.getBtsId(), device.getCellId());
    if (device.getDeviceModel() != null) {
      builder.withDeviceModelCode(device.getDeviceModel().getModelCode());
    }
    return builder.build();
  }
}
