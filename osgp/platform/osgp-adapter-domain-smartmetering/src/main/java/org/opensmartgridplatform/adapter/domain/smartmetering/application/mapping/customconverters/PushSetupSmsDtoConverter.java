//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;

public class PushSetupSmsDtoConverter
    extends AbstractPushSetupConverter<PushSetupSms, PushSetupSmsDto> {

  public PushSetupSmsDtoConverter(final ConfigurationMapper mapper) {
    super(mapper);
  }

  @Override
  public PushSetupSmsDto convert(
      final PushSetupSms source,
      final Type<? extends org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto>
          destinationType,
      final MappingContext context) {

    /*
     * Cast to PushSetupSmsDto should be fine, because the builder returned
     * from newBuilder is a PushSetupSmsDto.Builder, which builds a
     * PushSetupSmsDto.
     */
    return (PushSetupSmsDto) super.convert(source);
  }

  /*
   * This more specific return type should be fine as PushSetupSmsDto.Builder
   * extends AbstractBuilder<PushSetupSmsDto.Builder>
   */
  @SuppressWarnings("unchecked")
  @Override
  protected PushSetupSmsDto.Builder newBuilder() {
    return new PushSetupSmsDto.Builder();
  }
}
