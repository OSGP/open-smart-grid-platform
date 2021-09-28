/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.throttling.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.throttling.api.Client;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;

class ThrottlingMapperTest {

  private ThrottlingMapper throttlingMapper = new ThrottlingMapper();

  @Test
  void mapsApiConfigToNewEntity() {

    final Short id = null;
    final String name = "test-config-new-entity";
    final int maxConcurrency = 91;

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig expected =
        this.throttlingConfigEntity(id, name, maxConcurrency);
    final ThrottlingConfig source = this.throttlingConfigApi(id, name, maxConcurrency);

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig actual =
        this.throttlingMapper.map(
            source, org.opensmartgridplatform.throttling.entities.ThrottlingConfig.class);

    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  void mappingApiConfigToNewEntityIgnoresId() {

    final Short idApi = 910;
    final Short idEntity = null;
    final String name = "test-config-no-id-on-new";
    final int maxConcurrency = 3;

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig expected =
        this.throttlingConfigEntity(idEntity, name, maxConcurrency);
    final ThrottlingConfig source = this.throttlingConfigApi(idApi, name, maxConcurrency);

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig actual =
        this.throttlingMapper.map(
            source, org.opensmartgridplatform.throttling.entities.ThrottlingConfig.class);

    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  void mapsEntityConfigToApiConfig() {

    final Short id = 489;
    final String name = "test-config-entity-to-api";
    final int maxConcurrency = 349;

    final ThrottlingConfig expected = this.throttlingConfigApi(id, name, maxConcurrency);
    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig source =
        this.throttlingConfigEntity(id, name, maxConcurrency);

    final ThrottlingConfig actual = this.throttlingMapper.map(source, ThrottlingConfig.class);

    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  void updatesExistingEntityFromApiConfig() {

    final Short idApi = null;
    final Short idEntity = 3479;
    final String name = "test-config-update-entity";
    final int maxConcurrencyApi = 10;
    final int maxConcurrencyEntity = 15;

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig destination =
        this.throttlingConfigEntity(idEntity, name, maxConcurrencyEntity);
    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig expected =
        this.throttlingConfigEntity(idEntity, name, maxConcurrencyApi);
    final ThrottlingConfig source = this.throttlingConfigApi(idApi, name, maxConcurrencyApi);

    this.throttlingMapper.map(source, destination);

    assertThat(destination).isEqualToComparingFieldByField(expected);
  }

  @Test
  void updatingExistingConfigEntityFromApiIgnoresIdAndName() {

    final Short idApi = 1467;
    final Short idEntity = 57;
    final String nameApi = "test-config-update-entity-ignore-id-api";
    final String nameEntity = "test-config-update-entity-ignore-id";
    final int maxConcurrencyApi = 5;
    final int maxConcurrencyEntity = 3;

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig destination =
        this.throttlingConfigEntity(idEntity, nameEntity, maxConcurrencyEntity);
    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig expected =
        this.throttlingConfigEntity(idEntity, nameEntity, maxConcurrencyApi);
    final ThrottlingConfig source = this.throttlingConfigApi(idApi, nameApi, maxConcurrencyApi);

    this.throttlingMapper.map(source, destination);

    assertThat(destination).isEqualToComparingFieldByField(expected);
  }

  private ThrottlingConfig throttlingConfigApi(
      final Short id, final String name, final int maxConcurrency) {

    return new ThrottlingConfig(id, name, maxConcurrency);
  }

  private org.opensmartgridplatform.throttling.entities.ThrottlingConfig throttlingConfigEntity(
      final Short id, final String name, final int maxConcurrency) {

    return new org.opensmartgridplatform.throttling.entities.ThrottlingConfig(
        id, name, maxConcurrency);
  }

  @Test
  void mapsApiClientToNewEntity() {

    final Integer id = null;
    final String name = "test-client-new-entity";

    final org.opensmartgridplatform.throttling.entities.Client expected =
        this.clientEntity(id, name);
    final Client source = this.clientApi(id, name);

    final org.opensmartgridplatform.throttling.entities.Client actual =
        this.throttlingMapper.map(
            source, org.opensmartgridplatform.throttling.entities.Client.class);

    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  void mappingApiClientToNewEntityIgnoresId() {

    final Integer idApi = 4578;
    final Integer idEntity = null;
    final String name = "test-client-no-id-on-new";

    final org.opensmartgridplatform.throttling.entities.Client expected =
        this.clientEntity(idEntity, name);
    final Client source = this.clientApi(idApi, name);

    final org.opensmartgridplatform.throttling.entities.Client actual =
        this.throttlingMapper.map(
            source, org.opensmartgridplatform.throttling.entities.Client.class);

    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  void mapsEntityClientToApiClient() {

    final Integer id = 90185;
    final String name = "test-client-entity-to-api";
    final Instant registeredAt = Instant.now().minus(Duration.ofMinutes(13));
    final Instant unregisteredAt = null;

    final Client expected = this.clientApi(id, name, registeredAt, unregisteredAt);
    final org.opensmartgridplatform.throttling.entities.Client source =
        this.clientEntity(id, name, registeredAt, unregisteredAt);

    final Client actual = this.throttlingMapper.map(source, Client.class);

    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  void mapsUnregisteredEntityClientToApiClient() {

    final Integer id = 455021948;
    final String name = "test-client-unregistered-entity-to-api";
    final Instant registeredAt = Instant.now().minus(Duration.ofMinutes(458));
    final Instant unregisteredAt = registeredAt.plus(Duration.ofMinutes(425));

    final Client expected = this.clientApi(id, name, registeredAt, unregisteredAt);
    final org.opensmartgridplatform.throttling.entities.Client source =
        this.clientEntity(id, name, registeredAt, unregisteredAt);

    final Client actual = this.throttlingMapper.map(source, Client.class);

    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  void doesNotUpdateExistingEntityFromApiClient() {

    final Integer idApi = 45901;
    final Integer idEntity = 2234987;
    final String nameApi = "test-client-update-api";
    final String nameEntity = "test-client-update-entity";
    final Instant registeredAtApi = null;
    final Instant registeredAtEntity = Instant.now().minusSeconds(347980);
    final Instant unregisteredAtApi = Instant.now().minusMillis(727);
    final Instant unregisteredAtEntity = null;

    final org.opensmartgridplatform.throttling.entities.Client destination =
        this.clientEntity(idEntity, nameEntity, registeredAtEntity, unregisteredAtEntity);
    final org.opensmartgridplatform.throttling.entities.Client expected =
        this.clientEntity(idEntity, nameEntity, registeredAtEntity, unregisteredAtEntity);
    final Client source = this.clientApi(idApi, nameApi, registeredAtApi, unregisteredAtApi);

    this.throttlingMapper.map(source, destination);

    assertThat(destination).isEqualToComparingFieldByField(expected);
  }

  private Client clientApi(final Integer id, final String name) {
    return this.clientApi(id, name, null, null);
  }

  private Client clientApi(
      final Integer id,
      final String name,
      final Instant registeredAt,
      final Instant unregisteredAt) {

    return new Client(id, name, registeredAt, unregisteredAt);
  }

  private org.opensmartgridplatform.throttling.entities.Client clientEntity(
      final Integer id, final String name) {

    return this.clientEntity(id, name, null, null);
  }

  private org.opensmartgridplatform.throttling.entities.Client clientEntity(
      final Integer id,
      final String name,
      final Instant registeredAt,
      final Instant unregisteredAt) {

    return new org.opensmartgridplatform.throttling.entities.Client(
        id, name, registeredAt, unregisteredAt);
  }
}
