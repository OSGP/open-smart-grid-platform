// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec60870;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class Iec60870SimulatorApplicationTests {

  private final ApplicationContext applicationContext;

  Iec60870SimulatorApplicationTests(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Test
  void contextLoads() {
    assertThat(this.applicationContext).isNotNull();
  }
}
