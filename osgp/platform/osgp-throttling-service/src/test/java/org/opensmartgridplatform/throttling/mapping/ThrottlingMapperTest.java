// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;

class ThrottlingMapperTest {

  private final ThrottlingMapper throttlingMapper = new ThrottlingMapper();

  @Test
  void mapsApiConfigToNewEntity() {

    final Short id = null;
    final String name = "test-config-new-entity";
    final int maxConcurrency = 91;
    final int maxNewConnections = 351;
    final long maxNewConnectionsResetTimeInMs = 3000;
    final long maxNewConnectionsWaitTimeInMs = 4000;

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig expected =
        this.throttlingConfigEntity(
            id,
            name,
            maxConcurrency,
            maxNewConnections,
            maxNewConnectionsResetTimeInMs,
            maxNewConnectionsWaitTimeInMs);
    final ThrottlingConfig source =
        this.throttlingConfigApi(
            id,
            name,
            maxConcurrency,
            maxNewConnections,
            maxNewConnectionsResetTimeInMs,
            maxNewConnectionsWaitTimeInMs);

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
    final int maxNewConnections = 351;
    final long maxNewConnectionsResetTimeInMs = 3000;
    final long maxNewConnectionsWaitTimeInMs = 2000;

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig expected =
        this.throttlingConfigEntity(
            idEntity,
            name,
            maxConcurrency,
            maxNewConnections,
            maxNewConnectionsResetTimeInMs,
            maxNewConnectionsWaitTimeInMs);
    final ThrottlingConfig source =
        this.throttlingConfigApi(
            idApi,
            name,
            maxConcurrency,
            maxNewConnections,
            maxNewConnectionsResetTimeInMs,
            maxNewConnectionsWaitTimeInMs);

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
    final int maxNewConnections = 351;
    final long maxNewConnectionsResetTimeInMs = 3000;
    final long maxNewConnectionsWaitTimeInMs = 2000;

    final ThrottlingConfig expected =
        this.throttlingConfigApi(
            id,
            name,
            maxConcurrency,
            maxNewConnections,
            maxNewConnectionsResetTimeInMs,
            maxNewConnectionsWaitTimeInMs);
    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig source =
        this.throttlingConfigEntity(
            id,
            name,
            maxConcurrency,
            maxNewConnections,
            maxNewConnectionsResetTimeInMs,
            maxNewConnectionsWaitTimeInMs);

    final ThrottlingConfig actual = this.throttlingMapper.map(source, ThrottlingConfig.class);

    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  void updatesExistingEntityFromApiConfig() {

    final Short idApi = null;
    final Short idEntity = 3479;
    final String name = "test-config-update-entity";
    final int maxConcurrencyApi = 10;
    final int maxNewConnectionsApi = 12;
    final long maxNewConnectionsResetTimeInMsApi = 1000;
    final long maxNewConnectionsWaitTimeInMsApi = 1200;

    final int maxConcurrencyEntity = 15;
    final int maxNewConnectionsEntity = 8;
    final long maxNewConnectionsResetTimeInMsEntity = 2000;
    final long maxNewConnectionsWaitTimeInMsEntity = 2200;

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig destination =
        this.throttlingConfigEntity(
            idEntity,
            name,
            maxConcurrencyEntity,
            maxNewConnectionsEntity,
            maxNewConnectionsResetTimeInMsEntity,
            maxNewConnectionsWaitTimeInMsEntity);
    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig expected =
        this.throttlingConfigEntity(
            idEntity,
            name,
            maxConcurrencyApi,
            maxNewConnectionsApi,
            maxNewConnectionsResetTimeInMsApi,
            maxNewConnectionsWaitTimeInMsApi);
    final ThrottlingConfig source =
        this.throttlingConfigApi(
            idApi,
            name,
            maxConcurrencyApi,
            maxNewConnectionsApi,
            maxNewConnectionsResetTimeInMsApi,
            maxNewConnectionsWaitTimeInMsApi);

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
    final int maxNewConnectionsApi = 12;
    final long maxNewConnectionsResetTimeInMsApi = 1000;
    final long maxNewConnectionsWaitTimeInMsApi = 1200;

    final int maxConcurrencyEntity = 3;
    final int maxNewConnectionsEntity = 8;
    final long maxNewConnectionsResetTimeInMsEntity = 2000;
    final long maxNewConnectionsWaitTimeInMsEntity = 2200;

    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig destination =
        this.throttlingConfigEntity(
            idEntity,
            nameEntity,
            maxConcurrencyEntity,
            maxNewConnectionsEntity,
            maxNewConnectionsResetTimeInMsEntity,
            maxNewConnectionsWaitTimeInMsEntity);
    final org.opensmartgridplatform.throttling.entities.ThrottlingConfig expected =
        this.throttlingConfigEntity(
            idEntity,
            nameEntity,
            maxConcurrencyApi,
            maxNewConnectionsApi,
            maxNewConnectionsResetTimeInMsApi,
            maxNewConnectionsWaitTimeInMsApi);
    final ThrottlingConfig source =
        this.throttlingConfigApi(
            idApi,
            nameApi,
            maxConcurrencyApi,
            maxNewConnectionsApi,
            maxNewConnectionsResetTimeInMsApi,
            maxNewConnectionsWaitTimeInMsApi);

    this.throttlingMapper.map(source, destination);

    assertThat(destination).isEqualToComparingFieldByField(expected);
  }

  private ThrottlingConfig throttlingConfigApi(
      final Short id,
      final String name,
      final int maxConcurrency,
      final int maxNewConnections,
      final long maxNewConnectionsResetTimeInMs,
      final long maxNewConnectionsWaitTimeInMs) {

    return new ThrottlingConfig(
        id,
        name,
        maxConcurrency,
        maxNewConnections,
        maxNewConnectionsResetTimeInMs,
        maxNewConnectionsWaitTimeInMs);
  }

  private org.opensmartgridplatform.throttling.entities.ThrottlingConfig throttlingConfigEntity(
      final Short id,
      final String name,
      final int maxConcurrency,
      final int maxNewConnections,
      final long maxNewConnectionsResetTimeInMs,
      final long maxNewConnectionsWaitTimeInMs) {

    return new org.opensmartgridplatform.throttling.entities.ThrottlingConfig(
        id,
        name,
        maxConcurrency,
        maxNewConnections,
        maxNewConnectionsResetTimeInMs,
        maxNewConnectionsWaitTimeInMs);
  }
}
