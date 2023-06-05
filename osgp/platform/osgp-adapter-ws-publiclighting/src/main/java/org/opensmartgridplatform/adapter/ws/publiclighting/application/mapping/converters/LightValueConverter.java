// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue;

/**
 * If the value isOn is true, map all fields of the LightValue. If the value isOn is false, only map
 * isOn and index, don't map dimValue.
 */
public class LightValueConverter
    extends CustomConverter<
        org.opensmartgridplatform.domain.core.valueobjects.LightValue,
        org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue> {

  @Override
  public LightValue convert(
      final org.opensmartgridplatform.domain.core.valueobjects.LightValue source,
      final Type<? extends LightValue> destinationType,
      final MappingContext mappingContext) {

    if (source == null) {
      return null;
    }

    final LightValue lv = new LightValue();
    lv.setIndex(source.getIndex());
    lv.setOn(source.isOn());
    if (source.isOn()) {
      lv.setDimValue(source.getDimValue());
    }

    return lv;
  }
}
