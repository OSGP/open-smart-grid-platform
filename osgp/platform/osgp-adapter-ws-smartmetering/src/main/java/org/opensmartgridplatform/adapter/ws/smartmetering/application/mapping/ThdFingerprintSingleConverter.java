// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetThdFingerprintResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ThdFingerprint;

@Slf4j
public class ThdFingerprintSingleConverter
    extends CustomConverter<
        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetThdFingerprintResponse,
        GetThdFingerprintResponse> {

  @Override
  public GetThdFingerprintResponse convert(
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
              .GetThdFingerprintResponse
          source,
      final Type<? extends GetThdFingerprintResponse> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ThdFingerprint
        sourceThdFingerprint = source.getThdFingerprint();

    final GetThdFingerprintResponse result = new GetThdFingerprintResponse();

    final ThdFingerprint dest = ThdFingerprintConverter.getThdFingerprint(sourceThdFingerprint);

    result.setThdFingerprint(dest);

    return result;
  }
}
