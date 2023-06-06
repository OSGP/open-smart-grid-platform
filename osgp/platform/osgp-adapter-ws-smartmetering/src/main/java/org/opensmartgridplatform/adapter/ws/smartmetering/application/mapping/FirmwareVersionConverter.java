// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;

public class FirmwareVersionConverter
    extends BidirectionalConverter<
        FirmwareVersion,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersion> {

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersion
      convertTo(
          final FirmwareVersion source,
          final Type<
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                      .FirmwareVersion>
              destinationType,
          final MappingContext mappingContext) {

    if (source == null) {
      return null;
    }

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersion
        firmwareVersion =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .FirmwareVersion();
    firmwareVersion.setFirmwareModuleType(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareModuleType
            .valueOf(source.getFirmwareModuleType().getDescription()));
    firmwareVersion.setVersion(source.getVersion());

    return firmwareVersion;
  }

  @Override
  public FirmwareVersion convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersion
          source,
      final Type<FirmwareVersion> destinationType,
      final MappingContext mappingContext) {

    if (source == null) {
      return null;
    }

    final FirmwareModuleType type =
        FirmwareModuleType.forDescription(source.getFirmwareModuleType().name());
    final String version = source.getVersion();

    return new FirmwareVersion(type, version);
  }
}
