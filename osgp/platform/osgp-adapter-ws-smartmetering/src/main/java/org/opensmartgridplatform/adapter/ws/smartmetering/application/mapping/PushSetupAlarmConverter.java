//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.math.BigInteger;
import java.util.List;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObjectDefinition;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;

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

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm
        pushSetupAlarm =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .PushSetupAlarm();

    if (source.hasSendDestinationAndMethod()) {
      final SendDestinationAndMethod sendDestinationAndMethod =
          source.getSendDestinationAndMethod();
      final String destination = sendDestinationAndMethod.getDestination();
      if (!source.hasValidDestination()) {
        throw new IllegalArgumentException(
            "Unable to parse destination as \"<host>:<port>\": " + destination);
      }
      final String[] hostAndPort = destination.split(":");
      pushSetupAlarm.setHost(hostAndPort[0]);
      pushSetupAlarm.setPort(new BigInteger(hostAndPort[1]));
    }

    if (source.hasPushObjectList()) {
      final List<PushObject> convertedPushObjectList =
          this.mapperFacade.mapAsList(source.getPushObjectList(), PushObject.class);
      pushSetupAlarm.getPushObjectList().addAll(convertedPushObjectList);
    }

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

    if (source.getHost() != null && source.getPort() != null) {
      // Create send destination object. Note: TransportService and MessageType are set in protocol
      // adapter, based on the protocol version
      final String destination = source.getHost() + ":" + source.getPort();
      final SendDestinationAndMethod sendDestinationAndMethod =
          new SendDestinationAndMethod(null, destination, null);
      builder.withSendDestinationAndMethod(sendDestinationAndMethod);
    }

    final List<PushObject> sourcePushObjects = source.getPushObjectList();

    if (sourcePushObjects != null) {
      final List<CosemObjectDefinition> convertedPushObjectList =
          this.mapperFacade.mapAsList(source.getPushObjectList(), CosemObjectDefinition.class);
      builder.withPushObjectList(convertedPushObjectList);
    }

    return builder.build();
  }
}
