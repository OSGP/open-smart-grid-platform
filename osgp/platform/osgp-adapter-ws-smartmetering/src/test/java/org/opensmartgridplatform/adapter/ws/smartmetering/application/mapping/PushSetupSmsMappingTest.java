// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupSms;

public class PushSetupSmsMappingTest {

  private static final String HOST = "host";
  private static final BigInteger PORT = BigInteger.TEN;
  private static final String DESTINATION = "host:10";
  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

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

    // port and host are combined into destination. The converter sets null values for the other two
    // variables of a SendDestinationAndMethod.
    assertThat(pushSetupSmsMapped.getSendDestinationAndMethod().getDestination())
        .isEqualTo(DESTINATION);
    assertThat(pushSetupSmsMapped.getSendDestinationAndMethod().getTransportService()).isNull();
    assertThat(pushSetupSmsMapped.getSendDestinationAndMethod().getMessage()).isNull();

    // Only a SendDestinationAndMethod is mapped:
    assertThat(pushSetupSmsMapped.getLogicalName()).isNull();
    assertThat(pushSetupSmsMapped.getCommunicationWindow()).isNull();
    assertThat(pushSetupSmsMapped.getNumberOfRetries()).isNull();
    assertThat(pushSetupSmsMapped.getPushObjectList()).isNull();
    assertThat(pushSetupSmsMapped.getRandomisationStartInterval()).isNull();
    assertThat(pushSetupSmsMapped.getRepetitionDelay()).isNull();
  }
}
