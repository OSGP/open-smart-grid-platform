// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;

public class PushSetupAlarmDtoConverter
    extends AbstractPushSetupConverter<PushSetupAlarm, PushSetupAlarmDto> {

  public PushSetupAlarmDtoConverter(final ConfigurationMapper configurationMapper) {
    super(configurationMapper);
  }

  @Override
  public PushSetupAlarmDto convert(
      final PushSetupAlarm source,
      final Type<
              ? extends org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto>
          destinationType,
      final MappingContext context) {

    /*
     * Cast to PushSetupAlarmDto should be fine, because the builder
     * returned from newBuilder is a PushSetupAlarmDto.Builder, which builds
     * a PushSetupAlarmDto.
     */
    return (PushSetupAlarmDto) super.convert(source);
  }

  /*
   * This more specific return type should be fine as
   * PushSetupAlarmDto.Builder extends
   * AbstractBuilder<PushSetupAlarmDto.Builder>
   */
  @SuppressWarnings("unchecked")
  @Override
  protected PushSetupAlarmDto.Builder newBuilder() {
    return new PushSetupAlarmDto.Builder();
  }
}
