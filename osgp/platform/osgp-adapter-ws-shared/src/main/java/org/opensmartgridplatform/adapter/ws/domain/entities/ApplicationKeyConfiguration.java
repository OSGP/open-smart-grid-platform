// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.domain.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

@Entity
public class ApplicationKeyConfiguration implements Serializable {

  private static final long serialVersionUID = 7556366049796795779L;

  @EmbeddedId private ApplicationDataLookupKey id;
  private String publicKeyLocation;

  protected ApplicationKeyConfiguration() {
    // No-argument constructor, required for JPA entity classes.
  }

  public ApplicationKeyConfiguration(
      final ApplicationDataLookupKey id, final String publicKeyLocation) {

    this.id = Objects.requireNonNull(id, "id must not be null");

    if (StringUtils.isBlank(publicKeyLocation)) {
      throw new IllegalArgumentException("publicKeyLocation must not be null, empty or blank");
    } else {
      this.publicKeyLocation = publicKeyLocation;
    }
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

  public ApplicationDataLookupKey getId() {
    return this.id;
  }

  public void setId(final ApplicationDataLookupKey id) {
    this.id = Objects.requireNonNull(id, "id must not be null");
  }

  public String getPublicKeyLocation() {
    return this.publicKeyLocation;
  }

  public void setPublicKeyLocation(final String publicKeyLocation) {
    if (StringUtils.isBlank(publicKeyLocation)) {
      throw new IllegalArgumentException("publicKeyLocation must not be null, empty or blank");
    } else {
      this.publicKeyLocation = publicKeyLocation;
    }
  }
}
