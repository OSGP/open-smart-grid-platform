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

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusType;

public class AdministrativeStatusTypeMappingTest {

  private ConfigurationMapper configurationMapper = new ConfigurationMapper();

  /**
   * Both objects have the same name, but are in different packages. Mapping needs to be
   * bidirectional, so this tests mapping one way.
   */
  @Test
  public void testMappingOneWay() {

    // actual mapping
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .AdministrativeStatusType
        undefined =
            this.configurationMapper.map(
                AdministrativeStatusType.UNDEFINED,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                    .AdministrativeStatusType.class);
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .AdministrativeStatusType
        off =
            this.configurationMapper.map(
                AdministrativeStatusType.OFF,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                    .AdministrativeStatusType.class);
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .AdministrativeStatusType
        on =
            this.configurationMapper.map(
                AdministrativeStatusType.ON,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                    .AdministrativeStatusType.class);

    // check mapping
    assertThat(undefined).isNotNull();
    assertThat(off).isNotNull();
    assertThat(on).isNotNull();

    assertThat(undefined.name()).isEqualTo(AdministrativeStatusType.UNDEFINED.name());
    assertThat(off.name()).isEqualTo(AdministrativeStatusType.OFF.name());
    assertThat(on.name()).isEqualTo(AdministrativeStatusType.ON.name());
  }

  /**
   * Both objects have the same name, but are in different packages. Mapping needs to be
   * bidirectional, so this tests mapping the other way round.
   */
  @Test
  public void testMappingTheOtherWay() {

    // actual mapping
    final AdministrativeStatusType undefined =
        this.configurationMapper.map(
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .AdministrativeStatusType.UNDEFINED,
            AdministrativeStatusType.class);
    final AdministrativeStatusType off =
        this.configurationMapper.map(
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .AdministrativeStatusType.OFF,
            AdministrativeStatusType.class);
    final AdministrativeStatusType on =
        this.configurationMapper.map(
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .AdministrativeStatusType.ON,
            AdministrativeStatusType.class);

    // check mapping
    assertThat(undefined).isNotNull();
    assertThat(off).isNotNull();
    assertThat(on).isNotNull();

    assertThat(undefined.name())
        .isEqualTo(
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .AdministrativeStatusType.UNDEFINED
                .name());
    assertThat(off.name())
        .isEqualTo(
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .AdministrativeStatusType.OFF
                .name());
    assertThat(on.name())
        .isEqualTo(
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
                .AdministrativeStatusType.ON
                .name());
  }
}
