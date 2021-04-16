/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.math.BigInteger;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MessageType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.TransportServiceType;

public class PushSetupAlarmConverter
    extends BidirectionalConverter<
        PushSetupAlarm,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm> {

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm
      convertTo(
          final PushSetupAlarm source,
          final Type<
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                      .PushSetupAlarm>
              destinationType,
          final MappingContext context) {
    if (source == null) {
      return null;
    }
    if (!source.hasSendDestinationAndMethod()) {
      throw new IllegalArgumentException(
          "Unable to map PushSetupAlarm without SendDestinationAndMethod");
    }
    final SendDestinationAndMethod sendDestinationAndMethod = source.getSendDestinationAndMethod();
    final String destination = sendDestinationAndMethod.getDestination();
    if (!destination.matches("\\S++:\\d++")) {
      throw new IllegalArgumentException(
          "Unable to parse destination as \"<host>:<port>\": " + destination);
    }
    final String[] hostAndPort = destination.split(":");
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm
        pushSetupAlarm =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .PushSetupAlarm();
    pushSetupAlarm.setHost(hostAndPort[0]);
    pushSetupAlarm.setPort(new BigInteger(hostAndPort[1]));
    return pushSetupAlarm;
  }

  @Override
  public PushSetupAlarm convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm
          source,
      final Type<PushSetupAlarm> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }
    final PushSetupAlarm.Builder builder = new PushSetupAlarm.Builder();

    final String destination = source.getHost() + ":" + source.getPort();
    final SendDestinationAndMethod sendDestinationAndMethod =
        new SendDestinationAndMethod(
            TransportServiceType.TCP, destination, MessageType.MANUFACTURER_SPECIFIC);
    builder.withSendDestinationAndMethod(sendDestinationAndMethod);

    return builder.build();
  }
}
