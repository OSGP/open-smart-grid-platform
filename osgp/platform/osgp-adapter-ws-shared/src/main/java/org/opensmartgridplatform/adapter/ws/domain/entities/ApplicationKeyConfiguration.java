/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.adapter.ws.domain.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class ApplicationKeyConfiguration implements Serializable {

  private static final long serialVersionUID = 7556366049796795779L;

  @EmbeddedId private ApplicationKeyLookupKey id;
  private String publicKeyLocation;

  protected ApplicationKeyConfiguration() {
    // No-argument constructor, required for JPA entity classes.
  }

  public ApplicationKeyConfiguration(final ApplicationKeyLookupKey id) {

    this.id = Objects.requireNonNull(id, "id must not be null");
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ApplicationKeyConfiguration)) {
      return false;
    }
    final ApplicationKeyConfiguration other = (ApplicationKeyConfiguration) obj;
    return Objects.equals(this.getId(), other.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.getId());
  }

  @Override
  public String toString() {
    return String.format(
        "%s[%s, publicKeyLocation=%s]",
        this.getClass().getSimpleName(), this.getId(), this.getPublicKeyLocation());
  }

  public ApplicationKeyLookupKey getId() {
    return this.id;
  }

  public void setId(final ApplicationKeyLookupKey id) {
    this.id = id;
  }

  public String getPublicKeyLocation() {
    return this.publicKeyLocation;
  }

  public void setPublicKeyLocation(final String publicKeyLocation) {
    this.publicKeyLocation = publicKeyLocation;
  }
}
