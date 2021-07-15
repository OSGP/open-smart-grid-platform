/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
