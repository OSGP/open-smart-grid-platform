// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData;

public class SetKeysRequestConverter
    extends BidirectionalConverter<
        SetKeysRequestData,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetKeysRequestData> {

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData
      convertTo(
          final SetKeysRequestData source,
          final Type<
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                      .SetKeysRequestData>
              destinationType,
          final MappingContext context) {
    if (source == null) {
      return null;
    }

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData
        destination =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .SetKeysRequestData();
    destination.setAuthenticationKey(source.getAuthenticationKey());
    destination.setEncryptionKey(source.getEncryptionKey());

    return destination;
  }

  @Override
  public SetKeysRequestData convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
              .SetKeysRequestData
          source,
      final Type<SetKeysRequestData> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    return new SetKeysRequestData(source.getAuthenticationKey(), source.getEncryptionKey());
  }
}
