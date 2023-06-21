// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupLastGasp;

public class PushSetupLastGaspMappingTest {

  private static final String HOST = "host";
  private static final BigInteger PORT = BigInteger.TEN;
  private static final String DESTINATION = "host:10";
  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  /** Tests if a PushSetupLastGasp object can be mapped successfully. */
  @Test
  public void testPushSetupLastGaspMapping() {

    // build test data
    final PushSetupLastGasp pushSetupLastGaspOriginal = new PushSetupLastGasp();
    pushSetupLastGaspOriginal.setHost(HOST);
    pushSetupLastGaspOriginal.setPort(PORT);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupLastGasp
        pushSetupLastGaspMapped =
            this.configurationMapper.map(
                pushSetupLastGaspOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupLastGasp
                    .class);

    // port and host are combined into destination. The converter sets null values for the other two
    // variables of a SendDestinationAndMethod.
    assertThat(pushSetupLastGaspMapped.getSendDestinationAndMethod().getDestination())
        .isEqualTo(DESTINATION);
    assertThat(pushSetupLastGaspMapped.getSendDestinationAndMethod().getTransportService())
        .isNull();
    assertThat(pushSetupLastGaspMapped.getSendDestinationAndMethod().getMessage()).isNull();

    // Only a SendDestinationAndMethod is mapped:
    assertThat(pushSetupLastGaspMapped.getLogicalName()).isNull();
    assertThat(pushSetupLastGaspMapped.getCommunicationWindow()).isNull();
    assertThat(pushSetupLastGaspMapped.getNumberOfRetries()).isNull();
    assertThat(pushSetupLastGaspMapped.getPushObjectList()).isNull();
    assertThat(pushSetupLastGaspMapped.getRandomisationStartInterval()).isNull();
    assertThat(pushSetupLastGaspMapped.getRepetitionDelay()).isNull();
  }
}
