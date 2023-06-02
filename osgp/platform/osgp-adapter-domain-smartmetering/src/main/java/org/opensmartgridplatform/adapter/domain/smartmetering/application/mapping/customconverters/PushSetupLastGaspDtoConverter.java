//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupLastGasp;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupLastGaspDto;

public class PushSetupLastGaspDtoConverter
    extends AbstractPushSetupConverter<PushSetupLastGasp, PushSetupLastGaspDto> {

  public PushSetupLastGaspDtoConverter(final ConfigurationMapper mapper) {
    super(mapper);
  }

  @Override
  public PushSetupLastGaspDto convert(
      final PushSetupLastGasp source,
      final Type<
              ? extends
                  org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupLastGaspDto>
          destinationType,
      final MappingContext context) {

    /*
     * Cast to PushSetupLastGaspDto should be fine, because the builder returned
     * from newBuilder is a PushSetupLastGaspDto.Builder, which builds a
     * PushSetupLastGaspDto.
     */
    return (PushSetupLastGaspDto) super.convert(source);
  }

  /*
   * This more specific return type should be fine as PushSetupLastGaspDto.Builder
   * extends AbstractBuilder<PushSetupLastGaspDto.Builder>
   */
  @SuppressWarnings("unchecked")
  @Override
  protected PushSetupLastGaspDto.Builder newBuilder() {
    return new PushSetupLastGaspDto.Builder();
  }
}
