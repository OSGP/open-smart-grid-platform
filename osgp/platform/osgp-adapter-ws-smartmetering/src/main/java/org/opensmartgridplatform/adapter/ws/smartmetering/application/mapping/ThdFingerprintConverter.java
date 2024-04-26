// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.FingerprintValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetThdFingerprintResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ThdFingerprint;

public class ThdFingerprintConverter
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

    final ThdFingerprint dest = new ThdFingerprint();
    dest.setThdInstantaneousCurrentL1(sourceThdFingerprint.getThdInstantaneousCurrentL1());
    dest.setThdInstantaneousCurrentL2(sourceThdFingerprint.getThdInstantaneousCurrentL2());
    dest.setThdInstantaneousCurrentL3(sourceThdFingerprint.getThdInstantaneousCurrentL3());
    final FingerprintValues fingerprintValuesL1 = new FingerprintValues();
    fingerprintValuesL1
        .getFingerprintValue()
        .addAll(sourceThdFingerprint.getThdInstantaneousCurrentFingerprintL1());
    dest.setThdInstantaneousCurrentFingerprintL1(fingerprintValuesL1);
    final FingerprintValues fingerprintValuesL2 = new FingerprintValues();
    fingerprintValuesL2
        .getFingerprintValue()
        .addAll(sourceThdFingerprint.getThdInstantaneousCurrentFingerprintL2());
    dest.setThdInstantaneousCurrentFingerprintL2(fingerprintValuesL2);
    final FingerprintValues fingerprintValuesL3 = new FingerprintValues();
    fingerprintValuesL3
        .getFingerprintValue()
        .addAll(sourceThdFingerprint.getThdInstantaneousCurrentFingerprintL3());
    dest.setThdInstantaneousCurrentFingerprintL3(fingerprintValuesL3);
    dest.setThdCurrentOverLimitCounterL1(sourceThdFingerprint.getThdCurrentOverLimitCounterL1());
    dest.setThdCurrentOverLimitCounterL2(sourceThdFingerprint.getThdCurrentOverLimitCounterL2());
    dest.setThdCurrentOverLimitCounterL3(sourceThdFingerprint.getThdCurrentOverLimitCounterL3());

    result.setThdFingerprint(dest);

    return result;
  }
}
