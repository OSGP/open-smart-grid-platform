/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupSms;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MessageType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.TransportServiceType;

public class PushSetupSmsMappingTest {

  private static final String HOST = "host";
  private static final BigInteger PORT = BigInteger.TEN;
  private static final String DESTINATION = "host:10";
  private static final TransportServiceType TRANSPORTSERVICETYPE = TransportServiceType.TCP;
  private static final MessageType MESSAGETYPE = MessageType.MANUFACTURER_SPECIFIC;
  private ConfigurationMapper configurationMapper = new ConfigurationMapper();

  /** Tests if a PushSetupSms object can be mapped successfully. */
  @Test
  public void testPushSetupSmsMapping() {

    // build test data
    final PushSetupSms pushSetupSmsOriginal = new PushSetupSms();
    pushSetupSmsOriginal.setHost(HOST);
    pushSetupSmsOriginal.setPort(PORT);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms
        pushSetupSmsMapped =
            this.configurationMapper.map(
                pushSetupSmsOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms
                    .class);

    // check mapping
    assertThat(pushSetupSmsMapped).isNotNull();
    assertThat(pushSetupSmsMapped.getSendDestinationAndMethod()).isNotNull();
    assertThat(pushSetupSmsMapped.getSendDestinationAndMethod().getDestination()).isNotNull();
    assertThat(pushSetupSmsMapped.getSendDestinationAndMethod().getTransportService()).isNotNull();
    assertThat(pushSetupSmsMapped.getSendDestinationAndMethod().getMessage()).isNotNull();

    // Only a SendDestinationAndMethod is mapped:
    assertThat(pushSetupSmsMapped.getLogicalName()).isNull();
    assertThat(pushSetupSmsMapped.getCommunicationWindow()).isNull();
    assertThat(pushSetupSmsMapped.getNumberOfRetries()).isNull();
    assertThat(pushSetupSmsMapped.getPushObjectList()).isNull();
    assertThat(pushSetupSmsMapped.getRandomisationStartInterval()).isNull();
    assertThat(pushSetupSmsMapped.getRepetitionDelay()).isNull();

    // port and host are combined into destination. The converter sets
    // default values for the other two variables of a
    // SendDestinationAndMethod.
    assertThat(pushSetupSmsMapped.getSendDestinationAndMethod().getDestination())
        .isEqualTo(DESTINATION);
    assertThat(pushSetupSmsMapped.getSendDestinationAndMethod().getTransportService().name())
        .isEqualTo(TRANSPORTSERVICETYPE.name());
    assertThat(pushSetupSmsMapped.getSendDestinationAndMethod().getMessage().name())
        .isEqualTo(MESSAGETYPE.name());
  }
}
