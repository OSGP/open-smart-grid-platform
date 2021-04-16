/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class GetConfigurationObjectServiceSmr5Test {

  private GetConfigurationObjectServiceSmr5 instance;

  @Mock private GetResult getResult;
  @Mock private DataObject nonBitString;

  @BeforeEach
  public void setUp() {
    this.instance = new GetConfigurationObjectServiceSmr5(null);
    when(this.nonBitString.isBitString()).thenReturn(false);
  }

  @Test
  public void handles() {
    assertThat(this.instance.handles(Protocol.SMR_5_0_0)).isTrue();
    assertThat(this.instance.handles(Protocol.SMR_5_1)).isTrue();
    assertThat(this.instance.handles(Protocol.DSMR_4_2_2)).isFalse();
    assertThat(this.instance.handles(Protocol.OTHER_PROTOCOL)).isFalse();
    assertThat(this.instance.handles(null)).isFalse();
  }

  @Test
  public void getFlagType() {
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
  public void getConfigurationObjectResultDataNull() throws ProtocolAdapterException {
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
  public void getConfigurationObjectResultDataNotBitString() throws ProtocolAdapterException {

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
