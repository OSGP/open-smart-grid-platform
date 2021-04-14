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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MessageType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.TransportServiceType;

public class PushSetupAlarmMappingTest {

  private static final String HOST = "host";
  private static final BigInteger PORT = BigInteger.TEN;
  private static final String DESTINATION = "host:10";
  private static final TransportServiceType TRANSPORTSERVICETYPE = TransportServiceType.TCP;
  private static final MessageType MESSAGETYPE = MessageType.MANUFACTURER_SPECIFIC;
  private ConfigurationMapper configurationMapper = new ConfigurationMapper();

  /** Tests if mapping a PushSetupAlarm object succeeds. */
  @Test
  public void testPushSetupAlarmMapping() {

    // build test data
    final PushSetupAlarm pushSetupAlarmOriginal = new PushSetupAlarm();
    pushSetupAlarmOriginal.setHost(HOST);
    pushSetupAlarmOriginal.setPort(PORT);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm
        pushSetupAlarmMapped =
            this.configurationMapper.map(
                pushSetupAlarmOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm
                    .class);

    // check mapping
    assertThat(pushSetupAlarmMapped).isNotNull();
    assertThat(pushSetupAlarmMapped.getSendDestinationAndMethod()).isNotNull();
    assertThat(pushSetupAlarmMapped.getSendDestinationAndMethod().getDestination()).isNotNull();
    assertThat(pushSetupAlarmMapped.getSendDestinationAndMethod().getTransportService())
        .isNotNull();
    assertThat(pushSetupAlarmMapped.getSendDestinationAndMethod().getMessage()).isNotNull();

    // Only a SendDestinationAndMethod is mapped:
    assertThat(pushSetupAlarmMapped.getLogicalName()).isNull();
    assertThat(pushSetupAlarmMapped.getCommunicationWindow()).isNull();
    assertThat(pushSetupAlarmMapped.getNumberOfRetries()).isNull();
    assertThat(pushSetupAlarmMapped.getPushObjectList()).isNull();
    assertThat(pushSetupAlarmMapped.getRandomisationStartInterval()).isNull();
    assertThat(pushSetupAlarmMapped.getRepetitionDelay()).isNull();

    // port and host are combined into destination. The converter sets
    // default values for the other two variables of a
    // SendDestinationAndMethod.
    assertThat(pushSetupAlarmMapped.getSendDestinationAndMethod().getDestination())
        .isEqualTo(DESTINATION);
    assertThat(pushSetupAlarmMapped.getSendDestinationAndMethod().getTransportService().name())
        .isEqualTo(TRANSPORTSERVICETYPE.name());
    assertThat(pushSetupAlarmMapped.getSendDestinationAndMethod().getMessage().name())
        .isEqualTo(MESSAGETYPE.name());
  }
}
