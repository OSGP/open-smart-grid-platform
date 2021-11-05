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

public class NonUniqueRequestIdException extends RuntimeException implements ApiException {

  private static final long serialVersionUID = 1L;

  private final int clientId;
  private final int requestId;

  /**
   * @param clientId the ID of a throttling client for which a {@code requestId} was reused
   * @param requestId a supposedly unique ID of a permit request that was used with multiple open
   *     requests
   */
  public NonUniqueRequestIdException(final int clientId, final int requestId) {
    super(
        String.format(
            "A permit is already held by client[id=%d] for requestId: %d", clientId, requestId));
    this.clientId = clientId;
    this.requestId = requestId;
  }

  public int clientId() {
    return this.clientId;
  }

  public int requestId() {
    return this.requestId;
  }

  @Override
  public JsonNode asJsonNode() {
    return new ObjectMapper()
        .createObjectNode()
        .put("error", "non-unique-request-id")
        .put("clientId", this.clientId)
        .put("requestId", this.requestId);
  }

  @Override
  public int statusCode() {
    return 409; // HTTP status 409 Conflict
  }
}
