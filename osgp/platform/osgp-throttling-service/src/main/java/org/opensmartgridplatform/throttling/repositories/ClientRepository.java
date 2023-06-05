// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Clients aren't actually saved, but need to identify themselves uniquely, because each client can
 * have its own unique request IDs (unique constraint on client id + request id). This repository
 * hands out unique client IDs from the database sequence for each newly registered client.
 */
@Component
public class ClientRepository {
  private final JdbcTemplate jdbcTemplate;

  public ClientRepository(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Integer getNextClientId() {
    return this.jdbcTemplate.queryForObject("SELECT nextval('client_id_seq')", Integer.class);
  }
}
