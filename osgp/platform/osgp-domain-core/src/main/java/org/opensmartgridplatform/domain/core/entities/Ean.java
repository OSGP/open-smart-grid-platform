//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class Ean extends AbstractEntity {

  private static final long serialVersionUID = 2569469187462546946L;

  @ManyToOne()
  @JoinColumn(name = "device")
  private Device device;

  @Column(nullable = false)
  private Long code;

  @Column() private String description;

  public Ean() {
    // Default constructor
  }

  public Ean(final Device device, final Long code, final String description) {
    this.device = device;
    this.code = code;
    this.description = description;
  }

  public Device getDevice() {
    return this.device;
  }

  public void setDevice(final Device device) {
    this.device = device;
  }

  public Long getCode() {
    return this.code;
  }

  public void setCode(final Long code) {
    this.code = code;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) {
      return true;
    }

    boolean result = false;
    if (o instanceof Ean) {
      final Ean that = (Ean) o;

      // "code" is a unique identifier for an EAN, so check
      // equality for just that property:
      result = Objects.equals(this.code, that.code);
    }
    return result;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.device, this.code, this.description);
  }
}
