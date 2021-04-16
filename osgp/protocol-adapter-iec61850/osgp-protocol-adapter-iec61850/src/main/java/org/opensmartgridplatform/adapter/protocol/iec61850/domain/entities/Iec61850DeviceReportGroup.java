/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Table(name = "iec61850_device_report_group")
public class Iec61850DeviceReportGroup extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = 182081847594061747L;

  @Column(unique = true, nullable = false, length = 40)
  private String deviceIdentification;

  @ManyToOne()
  @JoinColumn(name = "report_group_id")
  private Iec61850ReportGroup iec61850ReportGroup;

  @Column(nullable = false)
  private boolean enabled;

  @Column(unique = true, nullable = false, length = 255)
  private String reportDataSet;

  @Column(unique = true, nullable = false, length = 255)
  private String domain;

  @Column(unique = true, nullable = false, length = 255)
  private String domainVersion;

  public Iec61850DeviceReportGroup() {
    // Default constructor
  }

  public Iec61850DeviceReportGroup(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  @Override
  public String toString() {
    return String.format(
        "Iec61850DeviceReportGroup[deviceIdentification=%s, reportGroup=%s, enabled=%s]",
        this.deviceIdentification, this.iec61850ReportGroup.getName(), this.enabled);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Iec61850DeviceReportGroup)) {
      return false;
    }

    final Iec61850DeviceReportGroup report = (Iec61850DeviceReportGroup) o;

    return Objects.equals(this.deviceIdentification, report.deviceIdentification)
        && Objects.equals(this.iec61850ReportGroup, report.iec61850ReportGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.deviceIdentification, this.iec61850ReportGroup);
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public Iec61850ReportGroup getIec61850ReportGroup() {
    return this.iec61850ReportGroup;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setIsEnabled(final boolean value) {
    this.enabled = value;
  }

  public String getReportDataSet() {
    return this.reportDataSet;
  }

  public String getDomain() {
    return this.domain;
  }

  public String getDomainVersion() {
    return this.domainVersion;
  }
}
