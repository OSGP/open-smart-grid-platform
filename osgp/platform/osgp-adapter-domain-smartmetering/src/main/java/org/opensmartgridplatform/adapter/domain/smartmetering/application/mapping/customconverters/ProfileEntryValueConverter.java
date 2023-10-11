// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.math.BigDecimal;
import java.util.Date;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;

public class ProfileEntryValueConverter
    extends CustomConverter<ProfileEntryValueDto, ProfileEntryValue> {

  @Override
  public ProfileEntryValue convert(
      final ProfileEntryValueDto source,
      final Type<? extends ProfileEntryValue> destinationType,
      final MappingContext context) {
    final Object value = source.getValue();
    if (value != null) {
      if (value instanceof final Long longValue) {
        return new ProfileEntryValue(longValue);
      } else if (value instanceof final Date date) {
        return new ProfileEntryValue(date);
      } else if (value instanceof final BigDecimal bigDecimal) {
        return new ProfileEntryValue(bigDecimal);
      } else if (value instanceof final Integer integer) {
        return new ProfileEntryValue(integer);
      } else if (value instanceof final Short shortValue) {
        return new ProfileEntryValue(shortValue);
      } else {
        return new ProfileEntryValue((String) value);
      }
    } else {
      return new ProfileEntryValue((String) null);
    }
  }
}
