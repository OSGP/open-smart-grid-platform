// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;

public class ReadAlarmRegisterDataConverter
    extends CustomConverter<ReadAlarmRegisterData, ReadAlarmRegisterDataDto> {

  @Override
  public ReadAlarmRegisterDataDto convert(
      final ReadAlarmRegisterData source,
      final Type<? extends ReadAlarmRegisterDataDto> destinationType,
      final MappingContext context) {
    return new ReadAlarmRegisterDataDto();
  }
}
