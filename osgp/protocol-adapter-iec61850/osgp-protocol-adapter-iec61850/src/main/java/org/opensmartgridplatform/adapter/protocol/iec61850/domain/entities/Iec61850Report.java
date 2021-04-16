/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Table(name = "iec61850_report")
public class Iec61850Report extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = 182081847594063328L;

  @Column(unique = true, nullable = false, length = 255)
  private String logicalDevice;

  @Column(unique = true, nullable = false, length = 255)
  private String logicalNode;

  @Column(unique = true, nullable = false, length = 255)
  private String name;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "iec61850Reports")
  private final Set<Iec61850ReportGroup> iec61850ReportGroups = new HashSet<>(0);

  public Iec61850Report() {
    // Default constructor
  }

  @Override
  public String toString() {
    return String.format(
        "Iec61850Report[logicalDevice=%s, logicalNode=%s, name=%s]",
        this.logicalDevice, this.logicalNode, this.name);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Iec61850Report)) {
      return false;
    }

    final Iec61850Report report = (Iec61850Report) o;

    return Objects.equals(this.logicalNode, report.logicalNode);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.logicalNode);
  }

  public String getLogicalDevice() {
    return this.logicalDevice;
  }

  public String getLogicalNode() {
    return this.logicalNode;
  }

  public String getName() {
    return this.name;
  }

  public Set<Iec61850ReportGroup> getIec61850ReportGroups() {
    return this.iec61850ReportGroups;
  }
}
