// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping.ws;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;

public class EventTypeConverter
    extends CustomConverter<
        EventType, org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.EventType> {
  @Override
  public org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.EventType convert(
      final EventType source,
      final Type<
              ? extends org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.EventType>
          destinationType,
      final MappingContext context) {
    return org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.EventType.fromValue(
        source.name());
  }
}
