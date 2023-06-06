// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.math.BigInteger;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupLastGasp;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;

public class PushSetupLastGaspConverter
    extends BidirectionalConverter<
        PushSetupLastGasp,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupLastGasp> {

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupLastGasp
      convertTo(
          final PushSetupLastGasp source,
          final Type<
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                      .PushSetupLastGasp>
              destinationType,
          final MappingContext context) {
    if (source == null) {
      return null;
    }
    if (!source.hasSendDestinationAndMethod()) {
      throw new IllegalArgumentException(
          "Unable to map PushSetup LastGasp without SendDestinationAndMethod.");
    }
    final SendDestinationAndMethod sendDestinationAndMethod = source.getSendDestinationAndMethod();
    final String destination = sendDestinationAndMethod.getDestination();
    if (!source.hasValidDestination()) {
      throw new IllegalArgumentException(
          "Unable to parse destination as \"<host>:<port>\": " + destination);
    }
    final String[] hostAndPort = destination.split(":");
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupLastGasp
        pushSetup =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .PushSetupLastGasp();
    pushSetup.setHost(hostAndPort[0]);
    pushSetup.setPort(new BigInteger(hostAndPort[1]));
    return pushSetup;
  }

  @Override
  public PushSetupLastGasp convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
              .PushSetupLastGasp
          source,
      final Type<PushSetupLastGasp> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }
    final PushSetupLastGasp.Builder builder = new PushSetupLastGasp.Builder();

    // Create send destination object. Note: TransportService and MessageType are set in protocol
    // adapter, based on the protocol version
    final String destination = source.getHost() + ":" + source.getPort();
    final SendDestinationAndMethod sendDestinationAndMethod =
        new SendDestinationAndMethod(null, destination, null);
    builder.withSendDestinationAndMethod(sendDestinationAndMethod);

    return builder.build();
  }
}
