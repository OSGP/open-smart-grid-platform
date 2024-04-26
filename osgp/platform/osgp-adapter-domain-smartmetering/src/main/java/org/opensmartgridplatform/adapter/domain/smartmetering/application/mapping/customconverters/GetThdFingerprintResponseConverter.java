// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetThdFingerprintResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ThdFingerprint;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetThdFingerprintResponseDto;

public class GetThdFingerprintResponseConverter
    extends CustomConverter<GetThdFingerprintResponseDto, GetThdFingerprintResponse> {

  @Override
  public GetThdFingerprintResponse convert(
      final GetThdFingerprintResponseDto source,
      final Type<? extends GetThdFingerprintResponse> destinationType,
      final MappingContext context) {

    if (source == null) {
      return null;
    }

    final ThdFingerprint thdFingerprint =
        new ThdFingerprint(
            source.getThdInstantaneousCurrentL1(),
            source.getThdInstantaneousCurrentL2(),
            source.getThdInstantaneousCurrentL3(),
            source.getThdInstantaneousCurrentFingerprintL1(),
            source.getThdInstantaneousCurrentFingerprintL2(),
            source.getThdInstantaneousCurrentFingerprintL3(),
            source.getThdInstantaneousCurrentL1(),
            source.getThdCurrentOverLimitCounterL2(),
            source.getThdInstantaneousCurrentL3());

    return new GetThdFingerprintResponse(thdFingerprint);
  }
}
