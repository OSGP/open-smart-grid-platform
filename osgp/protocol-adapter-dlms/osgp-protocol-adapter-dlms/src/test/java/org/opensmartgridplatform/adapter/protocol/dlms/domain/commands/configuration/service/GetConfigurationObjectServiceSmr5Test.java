// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

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
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetConfigurationObjectServiceSmr5Test {

  private GetConfigurationObjectServiceSmr5 instance;

  @Mock private GetResult getResult;
  @Mock private DataObject nonBitString;

  @BeforeEach
  void setUp() {
    this.instance = new GetConfigurationObjectServiceSmr5(null, null, null);
    when(this.nonBitString.isBitString()).thenReturn(false);
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  @NullSource
  void handles(final Protocol protocol) {
    assertThat(this.instance.handles(protocol)).isEqualTo(protocol != null && protocol.isSmr5());
  }

  @Test
  void getFlagType() {
    for (final ConfigurationFlagTypeDto flagTypeDto : ConfigurationFlagTypeDto.values()) {
      flagTypeDto
          .getBitPositionSmr5()
          .ifPresent(
              bitPosition ->
                  assertThat(
                          this.instance
                              .getFlagType(bitPosition)
                              .orElseThrow(IllegalArgumentException::new))
                      .isEqualTo(flagTypeDto));
    }
  }

  @Test
  void getConfigurationObjectResultDataNull() throws ProtocolAdapterException {
    // SETUP
    when(this.getResult.getResultData()).thenReturn(null);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.instance.getConfigurationObject(this.getResult);
            });
  }

  @Test
  void getConfigurationObjectResultDataNotBitString() throws ProtocolAdapterException {

    // SETUP
    when(this.getResult.getResultData()).thenReturn(this.nonBitString);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.instance.getConfigurationObject(this.getResult);
            });
  }

  // happy flows covered in IT's
}
