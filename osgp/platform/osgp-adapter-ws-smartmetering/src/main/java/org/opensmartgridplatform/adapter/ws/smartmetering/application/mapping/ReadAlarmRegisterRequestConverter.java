/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;

public class ReadAlarmRegisterRequestConverter
    extends BidirectionalConverter<
        ReadAlarmRegisterRequest,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .ReadAlarmRegisterRequest> {

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
          .ReadAlarmRegisterRequest
      convertTo(
          final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                  .ReadAlarmRegisterRequest
              source,
          final Type<
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                      .ReadAlarmRegisterRequest>
              destinationType,
          final MappingContext context) {

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .ReadAlarmRegisterRequest
        destination =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                .ReadAlarmRegisterRequest();
    destination.setDeviceIdentification(source.getDeviceIdentification());

    return destination;
  }

  @Override
  public ReadAlarmRegisterRequest convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
              .ReadAlarmRegisterRequest
          source,
      final Type<
              org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                  .ReadAlarmRegisterRequest>
          destinationType,
      final MappingContext context) {

    return new ReadAlarmRegisterRequest(source.getDeviceIdentification());
  }
}
