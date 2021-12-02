/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
