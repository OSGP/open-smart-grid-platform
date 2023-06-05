// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Manufacturer entity class */
@Entity
public class Manufacturer implements Comparable<Manufacturer>, Serializable {

  private static final long serialVersionUID = 2822785411317646350L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 4)
  private String code;

  @Column(nullable = false, length = 50)
  private String name;

  @Column private boolean usePrefix;

  public Manufacturer() {
    // Default constructor
  }

  public Manufacturer(final String code, final String name, final boolean usePrefix) {
    this.code = code;
    this.name = name;
    this.usePrefix = usePrefix;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Manufacturer)) {
      return false;
    }
    final Manufacturer other = (Manufacturer) obj;
    return Objects.equals(this.code, other.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.code);
  }

  @Override
  public int compareTo(final Manufacturer o) {
    return this.code.compareTo(o.code);
  }

  @Override
  public String toString() {
    return String.format("Manufacturer[%s]", this.code);
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public boolean isUsePrefix() {
    return this.usePrefix;
  }

  public void setUsePrefix(final boolean usePrefix) {
    this.usePrefix = usePrefix;
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(final String code) {
    this.code = code;
  }
}
