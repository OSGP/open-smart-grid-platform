// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;

/**
 * FirmwareModule entity class holds the description of a specific firmware module of which
 * different versions can be part of firmware files that are installed on devices.
 */
@Entity
public class FirmwareModule implements Comparable<FirmwareModule>, Serializable {

  private static final long serialVersionUID = 1433265533431664370L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, updatable = false)
  private String description;

  protected FirmwareModule() {
    // No-argument constructor, not to be used by application code.
  }

  public FirmwareModule(final String description) {
    this.description = description;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof FirmwareModule)) {
      return false;
    }
    final FirmwareModule other = (FirmwareModule) obj;
    return Objects.equals(this.description, other.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.description);
  }

  @Override
  public int compareTo(final FirmwareModule o) {
    if (o == null) {
      return -1;
    }

    return ObjectUtils.compare(this.description, o.description);
  }

  @Override
  public String toString() {
    return String.format("FirmwareModule[%s]", this.description);
  }

  public Long getId() {
    return this.id;
  }

  public String getDescription() {
    return this.description;
  }
}
