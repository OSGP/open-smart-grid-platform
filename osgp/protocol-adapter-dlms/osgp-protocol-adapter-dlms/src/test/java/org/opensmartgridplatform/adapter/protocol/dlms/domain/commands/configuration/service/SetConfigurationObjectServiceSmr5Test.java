// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;

@ExtendWith(MockitoExtension.class)
public class SetConfigurationObjectServiceSmr5Test {

  private SetConfigurationObjectServiceSmr5 instance;

  @Mock private GetResult getResult;

  @BeforeEach
  public void setUp() {
    this.instance = new SetConfigurationObjectServiceSmr5(null);
  }

  @Test
  public void handles() {
    assertThat(this.instance.handles(Protocol.SMR_5_0_0)).isTrue();
    assertThat(this.instance.handles(Protocol.SMR_5_1)).isTrue();
    assertThat(this.instance.handles(Protocol.SMR_5_2)).isTrue();
    assertThat(this.instance.handles(Protocol.DSMR_4_2_2)).isFalse();
    assertThat(this.instance.handles(Protocol.OTHER_PROTOCOL)).isFalse();
    assertThat(this.instance.handles(null)).isFalse();
  }

  @Test
  public void getBitPosition() {
    for (final ConfigurationFlagTypeDto flagTypeDto : ConfigurationFlagTypeDto.values()) {
      flagTypeDto
          .getBitPositionSmr5()
          .ifPresent(
              bitPosition ->
                  assertThat(
                          this.instance
                              .getBitPosition(flagTypeDto)
                              .orElseThrow(IllegalArgumentException::new))
                      .isEqualTo(bitPosition));
    }
  }

  // happy flows covered in IT's
}
