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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.Instant;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class Client {

  @Positive private Integer id;

  @NotBlank private String name;

  private Instant registeredAt;
  private Instant unregisteredAt;
  private Instant lastSeenAt;

  public Client() {
    this(UUID.randomUUID().toString());
  }

  public Client(final String name) {
    this(null, name);
  }

  public Client(final Integer id, final String name) {
    this(id, name, null, null, null);
  }

  public Client(
      final Integer id,
      final String name,
      final Instant registeredAt,
      final Instant unregisteredAt,
      final Instant lastSeenAt) {

    this.id = id;
    this.name = name;
    this.registeredAt = registeredAt;
    this.unregisteredAt = unregisteredAt;
    this.lastSeenAt = lastSeenAt;
  }

  public Integer getId() {
    return this.id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Instant getRegisteredAt() {
    return this.registeredAt;
  }

  public void setRegisteredAt(final Instant registeredAt) {
    this.registeredAt = registeredAt;
  }

  public Instant getUnregisteredAt() {
    return this.unregisteredAt;
  }

  public void setUnregisteredAt(final Instant unregisteredAt) {
    this.unregisteredAt = unregisteredAt;
  }

  public Instant getLastSeenAt() {
    return this.lastSeenAt;
  }

  public void setLastSeenAt(final Instant lastSeenAt) {
    this.lastSeenAt = lastSeenAt;
  }

  @Override
  public String toString() {
    return String.format(
        "%s[id=%s, name=%s, registered=%s, unregistered=%s]",
        Client.class.getSimpleName(), this.id, this.name, this.registeredAt, this.unregisteredAt);
  }
}
