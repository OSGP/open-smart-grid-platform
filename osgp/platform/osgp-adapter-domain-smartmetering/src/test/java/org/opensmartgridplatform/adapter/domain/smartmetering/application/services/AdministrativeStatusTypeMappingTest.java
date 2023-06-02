//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;

public class AdministrativeStatusTypeMappingTest {

  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  // To see if mapping succeeds when a value is set to undefined.
  @Test
  public void testForValueUndefined() {

    // build test data
    final AdministrativeStatusType administrativeStatusType = AdministrativeStatusType.UNDEFINED;

    // actual mapping
    final AdministrativeStatusTypeDto administrativeStatusTypeDto =
        this.configurationMapper.map(administrativeStatusType, AdministrativeStatusTypeDto.class);

    // check if value is mapped correctly
    assertThat(administrativeStatusTypeDto).isNotNull();
    assertThat(administrativeStatusTypeDto.name()).isEqualTo(administrativeStatusType.name());
  }

  // To see if mapping succeeds when a value is set to On.
  @Test
  public void testForValueOn() {

    // build test data
    final AdministrativeStatusType administrativeStatusType = AdministrativeStatusType.ON;

    // actual mapping
    final AdministrativeStatusTypeDto administrativeStatusTypeDto =
        this.configurationMapper.map(administrativeStatusType, AdministrativeStatusTypeDto.class);

    // check if value is mapped correctly
    assertThat(administrativeStatusTypeDto).isNotNull();
    assertThat(administrativeStatusTypeDto.name()).isEqualTo(administrativeStatusType.name());
  }

  // To see if mapping succeeds when a value is set to Off.
  @Test
  public void testForValueOff() {

    // build test data
    final AdministrativeStatusType administrativeStatusType = AdministrativeStatusType.OFF;

    // actual mapping
    final AdministrativeStatusTypeDto administrativeStatusTypeDto =
        this.configurationMapper.map(administrativeStatusType, AdministrativeStatusTypeDto.class);

    // check if value is mapped correctly
    assertThat(administrativeStatusTypeDto).isNotNull();
    assertThat(administrativeStatusTypeDto.name()).isEqualTo(administrativeStatusType.name());
  }

  // check if mapping succeeds if the object is null.
  @Test
  public void testNull() {

    // build test data
    final AdministrativeStatusType administrativeStatusType = null;

    // actual mapping
    final AdministrativeStatusTypeDto administrativeStatusTypeDto =
        this.configurationMapper.map(administrativeStatusType, AdministrativeStatusTypeDto.class);

    // check if value is mapped correctly
    assertThat(administrativeStatusTypeDto).isNull();
  }
}
