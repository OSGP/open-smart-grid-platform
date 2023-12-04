// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SetConfigurationObjectServiceDsmr43Test {

  private static final GprsOperationModeTypeDto GPRS_OPERATION_MODE =
      GprsOperationModeTypeDto.ALWAYS_ON;

  private SetConfigurationObjectServiceDsmr43 instance;

  @Mock private GetResult getResult;
  @Mock ConfigurationObjectDto configurationToSet;
  @Mock ConfigurationObjectDto configurationOnDevice;

  @BeforeEach
  void setUp() {
    this.instance = new SetConfigurationObjectServiceDsmr43(null, null);
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  @NullSource
  void handles(final Protocol protocol) {
    assertThat(this.instance.handles(protocol)).isEqualTo(protocol != null && protocol.isDsmr43());
  }

  @Test
  void getBitPosition() {
    for (final ConfigurationFlagTypeDto flagTypeDto : ConfigurationFlagTypeDto.values()) {
      flagTypeDto
          .getBitPositionDsmr43()
          .ifPresent(
              bitPosition ->
                  assertThat(
                          this.instance
                              .getBitPosition(flagTypeDto)
                              .orElseThrow(IllegalArgumentException::new))
                      .isEqualTo(bitPosition));
    }
  }
}
