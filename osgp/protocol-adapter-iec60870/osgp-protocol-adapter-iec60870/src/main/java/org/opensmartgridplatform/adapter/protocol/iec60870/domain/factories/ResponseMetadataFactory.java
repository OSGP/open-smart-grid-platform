/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResponseMetadataFactory {

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  public ResponseMetadata createWithNewCorrelationUid(final ResponseMetadata responseMetadata) {
    final String organisationIdentification = responseMetadata.getOrganisationIdentification();
    final String deviceIdentification = responseMetadata.getDeviceIdentification();
    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);
    return new ResponseMetadata.Builder(responseMetadata)
        .withCorrelationUid(correlationUid)
        .build();
  }
}
