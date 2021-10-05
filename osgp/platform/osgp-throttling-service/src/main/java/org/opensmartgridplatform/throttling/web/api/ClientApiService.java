/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.throttling.web.api;

import org.opensmartgridplatform.throttling.repositories.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
@Transactional
public class ClientApiService {
  private final ClientRepository clientRepository;

  public ClientApiService(final ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  /**
   * Find the client in the database and mark the client as 'seen'.
   *
   * @param clientId ID of the client
   * @return Client object
   */
  public org.opensmartgridplatform.throttling.entities.Client getAndNoticeClient(
      final int clientId) {
    final org.opensmartgridplatform.throttling.entities.Client client =
        this.clientRepository
            .findById(clientId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No client found for ID: " + clientId));
    client.seen();
    return client;
  }
}
