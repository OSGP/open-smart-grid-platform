// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
