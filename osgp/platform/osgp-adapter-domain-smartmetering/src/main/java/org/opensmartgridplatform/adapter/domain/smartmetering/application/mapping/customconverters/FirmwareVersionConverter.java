//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;

public class FirmwareVersionConverter extends CustomConverter<FirmwareVersionDto, FirmwareVersion> {

  @Override
  public FirmwareVersion convert(
      final FirmwareVersionDto source,
      final Type<? extends FirmwareVersion> destinationType,
      final MappingContext context) {

    if (source == null) {
      return null;
    }

    return new FirmwareVersion(
        org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType.valueOf(
            source.getFirmwareModuleType().name()),
        source.getVersion());
  }
}
