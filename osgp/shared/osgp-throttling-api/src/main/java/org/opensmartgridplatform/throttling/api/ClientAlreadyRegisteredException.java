/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.throttling.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientAlreadyRegisteredException extends RuntimeException implements ApiException {

  private static final long serialVersionUID = 1L;

  private final String identity;

  /** @param identity the identity of a throttling client */
  public ClientAlreadyRegisteredException(final String identity) {
    super(
        String.format("A client with the provided identity was already registered: %s", identity));
    this.identity = identity;
  }

  public String identity() {
    return this.identity;
  }

  @Override
  public JsonNode asJsonNode() {
    return new ObjectMapper()
        .createObjectNode()
        .put("error", "client-already-registered")
        .put("name", this.identity);
  }

  @Override
  public int statusCode() {
    return 409; // HTTP status 409 Conflict
  }
}
