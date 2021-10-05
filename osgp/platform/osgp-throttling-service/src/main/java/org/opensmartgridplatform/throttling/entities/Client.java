/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.throttling.entities;

import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import org.hibernate.annotations.NaturalId;

@Entity
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @Column(nullable = false, updatable = false, unique = true)
  private String name;

  @Column(nullable = false, updatable = false)
  private Instant registeredAt;

  private Instant unregisteredAt;

  @Column(nullable = false)
  private Instant lastSeenAt;

  public Client() {
    // no-arg constructor required by JPA specification
  }

  public Client(final String name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
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
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.registeredAt = registeredAt;
    this.unregisteredAt = unregisteredAt;
    this.lastSeenAt = lastSeenAt;
  }

  public Integer getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public Instant getRegisteredAt() {
    return this.registeredAt;
  }

  @PrePersist
  public void prePersist() {
    this.registeredAt = Instant.now();
  }

  public Instant getUnregisteredAt() {
    return this.unregisteredAt;
  }

  public void unregister() {
    if (this.unregisteredAt != null) {
      throw new IllegalStateException(
          "Client has already been unregistered at " + this.unregisteredAt);
    }
    this.unregisteredAt = Instant.now();
  }

  public Instant getLastSeenAt() {
    return this.lastSeenAt;
  }

  public void seen() {
    this.lastSeenAt = Instant.now();
  }

  @Override
  public String toString() {
    return String.format(
        "%s[id=%s, name=%s, registered=%s, unregistered=%s]",
        Client.class.getSimpleName(), this.id, this.name, this.registeredAt, this.unregisteredAt);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Client)) {
      return false;
    }
    final Client other = (Client) obj;
    return Objects.equals(this.name, other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name);
  }
}
