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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.validation.Valid;
import org.opensmartgridplatform.throttling.api.Client;
import org.opensmartgridplatform.throttling.api.ClientAlreadyRegisteredException;
import org.opensmartgridplatform.throttling.mapping.ThrottlingMapper;
import org.opensmartgridplatform.throttling.repositories.ClientRepository;
import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/clients", produces = "application/json")
public class ClientController {

  private static final int PAGING_MIN_PAGE = 0;
  private static final int PAGING_MIN_SIZE = 1;
  private static final int PAGING_MAX_SIZE = 100;

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

  private final Lock clientLock = new ReentrantLock();

  private final ThrottlingMapper throttlingMapper;
  private final ClientRepository clientRepository;
  private final PermitRepository permitRepository;

  public ClientController(
      final ThrottlingMapper throttlingMapper,
      final ClientRepository clientRegistrationRepository,
      final PermitRepository permitRepository) {
    this.throttlingMapper = throttlingMapper;
    this.clientRepository = clientRegistrationRepository;
    this.permitRepository = permitRepository;
  }

  @GetMapping
  public ResponseEntity<List<Client>> clients(
      @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
      @RequestParam(name = "size", required = false, defaultValue = "10") final int size) {

    if (page < PAGING_MIN_PAGE || size < PAGING_MIN_SIZE) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    final Page<org.opensmartgridplatform.throttling.entities.Client> pageOfClients =
        this.clientRepository.findAll(
            PageRequest.of(page, Math.min(PAGING_MAX_SIZE, size), Sort.by("id").ascending()));

    final List<Client> clients =
        this.throttlingMapper.mapAsList(pageOfClients.getContent(), Client.class);

    return ResponseEntity.ok(clients);
  }

  /**
   * Register a client to participate in requesting and releasing permits for throttled network
   * access. By having clients register themselves instead of remaining anonymous, some actions can
   * be taken to release permits held by a specific client in case that client should somehow
   * abruptly disappear. Client registrations should be done with a name that uniquely identifies
   * the client.
   *
   * <p>Upon registration an ID is returned that should be used to identify the client in further
   * requests.
   *
   * <p>When a client is done requesting and releasing permits for network access, {@link
   * #unregisterClient(int) unregister} the client to let the throttling service know this client
   * should no longer hold, nor request any more permits.
   *
   * @param client The client to register
   * @return the {@code clientId} to be provided when requesting or releasing permits for the {@code
   *     client}
   */
  @PostMapping
  public ResponseEntity<Integer> registerClient(@Valid @RequestBody final Client client) {

    final org.opensmartgridplatform.throttling.entities.Client clientEntity;
    this.clientLock.lock();
    try {
      clientEntity =
          this.clientRepository
              .findOneByName(client.getName())
              .orElseGet(this.newClientEntityFromApi(client));

      if (clientEntity.getId() == null) {
        this.clientRepository.saveAndFlush(clientEntity);
      } else {
        throw new ClientAlreadyRegisteredException(client.getName());
      }
    } finally {
      this.clientLock.unlock();
    }

    return ResponseEntity.ok(clientEntity.getId());
  }

  /**
   * Unregister a client identified by the ID returned when it was {@link #registerClient(Client)
   * registered}.
   *
   * <p>When calling this method a client claims it no longer (intentionally) holds, nor will it
   * request any more permits for throttled network access.
   *
   * @param clientId the {@code clientId} this client received upon registration
   * @return the time of unregistration in millis since the epoch, with HTTP status {@code 202
   *     ACCEPTED}
   */
  @Transactional
  @DeleteMapping(path = "/{clientId}")
  public ResponseEntity<JsonNode> unregisterClient(@PathVariable final int clientId) {
    final org.opensmartgridplatform.throttling.entities.Client client = this.getClient(clientId);
    if (client.getUnregisteredAt() == null) {
      client.unregister();
    }
    final long numberOfPermits = this.permitRepository.countByClientId(clientId);
    if (numberOfPermits > 0) {
      LOGGER.warn(
          "Client {} unregistered with {} remaining permits.", client.getName(), numberOfPermits);
    }
    return ResponseEntity.accepted()
        .body(LongNode.valueOf(client.getUnregisteredAt().toEpochMilli()));
  }

  /**
   * Notice that client is still alive
   *
   * @param clientId ID of the client that is still alive
   * @return HTTP OK or NOT_FOUND based on clientId
   */
  @Transactional
  @PutMapping(path = "/{clientId}")
  public HttpStatus noticeClient(@PathVariable final int clientId) {
    final org.opensmartgridplatform.throttling.entities.Client client = this.getClient(clientId);
    client.seen();
    return HttpStatus.OK;
  }

  private org.opensmartgridplatform.throttling.entities.Client getClient(final int clientId) {
    return this.clientRepository
        .findById(clientId)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No client found for ID: " + clientId));
  }

  private Supplier<? extends org.opensmartgridplatform.throttling.entities.Client>
      newClientEntityFromApi(final Client client) {

    return () -> {
      if (client.getId() != null
          || client.getRegisteredAt() != null
          || client.getUnregisteredAt() != null) {
        LOGGER.warn(
            "Creating new client, ignoring ID, and instants of registration or unregistration from API request: {}",
            client);
      }
      return this.throttlingMapper.map(
          client, org.opensmartgridplatform.throttling.entities.Client.class);
    };
  }
}
