// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ThdConfiguration;

public class ThdConfigurationConverter
    extends BidirectionalConverter<
        ThdConfiguration,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ThdConfiguration> {

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ThdConfiguration
      convertTo(
          final ThdConfiguration source,
          final Type<
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                      .ThdConfiguration>
              destinationType,
          final MappingContext context) {
    if (source == null) {
      return null;
    }
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ThdConfiguration
        configuration =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .ThdConfiguration();
    configuration.setThdValueHysteresis(source.getValueHysteresis());
    configuration.setThdTimeThreshold(source.getTimeThreshold());
    configuration.setThdValueThreshold(source.getValueThreshold());
    configuration.setThdMinDurationOverToNormal(source.getMinDurationNormalToOver());
    configuration.setThdMinDurationNormalToOver(source.getMinDurationOverToNormal());
    return configuration;
  }

  @Override
  public ThdConfiguration convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ThdConfiguration
          source,
      final Type<ThdConfiguration> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }
    final ThdConfiguration.Builder builder = new ThdConfiguration.Builder();
    builder.withValueHysteresis(source.getThdValueHysteresis());
    builder.withTimeThreshold(source.getThdTimeThreshold());
    builder.withValueThreshold(source.getThdValueThreshold());
    builder.withMinDurationOverToNormal(source.getThdMinDurationOverToNormal());
    builder.withMinDurationNormalToOver(source.getThdMinDurationOverToNormal());
    return builder.build();
  }
}
