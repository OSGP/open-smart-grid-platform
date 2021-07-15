/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories;

import org.openmuc.j60870.ASdu;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.springframework.stereotype.Component;

@Component
public class LogItemFactory {
  public LogItem create(
      final ASdu asdu, final ResponseMetadata responseMetadata, final boolean incoming) {
    return new LogItem(
        responseMetadata.getDeviceIdentification(),
        responseMetadata.getOrganisationIdentification(),
        incoming,
        asdu.toString());
  }

  public LogItem create(
      final ASdu asdu,
      final String deviceIdentification,
      final String organisationIdentification,
      final boolean incoming) {
    return new LogItem(deviceIdentification, organisationIdentification, incoming, asdu.toString());
  }
}
