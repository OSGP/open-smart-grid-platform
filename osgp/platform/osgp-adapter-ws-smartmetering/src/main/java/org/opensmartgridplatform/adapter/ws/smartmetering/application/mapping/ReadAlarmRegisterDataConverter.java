/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterData;

public class ReadAlarmRegisterDataConverter
    extends CustomConverter<
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterData,
        ReadAlarmRegisterData> {

  @Override
  public ReadAlarmRegisterData convert(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
              .ReadAlarmRegisterData
          source,
      final Type<? extends ReadAlarmRegisterData> destinationType,
      final MappingContext context) {
    return new ReadAlarmRegisterData();
  }
}
