// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class RtuDevice extends Device {

  private static final long serialVersionUID = -5356111084356341244L;

  @Column private Instant lastCommunicationTime = Instant.EPOCH;

  @ManyToOne()
  @JoinColumn(name = "domain_info_id")
  private DomainInfo domainInfo;

  public RtuDevice() {
    // No-arg constructor for frameworks.
  }

  public RtuDevice(final String deviceIdentification) {
    super(deviceIdentification);
  }

  public void messageReceived() {
    this.lastCommunicationTime = Instant.now();
  }

  public void messageReceived(final Instant instant) {
    this.lastCommunicationTime = instant;
  }

  public Instant getLastCommunicationTime() {
    return this.lastCommunicationTime;
  }

  public void setDomainInfo(final DomainInfo domainInfo) {
    this.domainInfo = domainInfo;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final RtuDevice device = (RtuDevice) o;
    return Objects.equals(this.deviceIdentification, device.deviceIdentification);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.deviceIdentification);
  }
}
