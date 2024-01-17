// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.db.api.iec61850.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class ProtocolInfo implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 6141430733483765789L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  protected Long id;

  @Column(nullable = false, length = 255)
  private String protocol;

  @Column(nullable = false, length = 255)
  private String protocolVersion;

  protected ProtocolInfo() {
    // Default constructor
  }

  public ProtocolInfo(final String protocol, final String protocolVersion) {
    this.protocol = protocol;
    this.protocolVersion = protocolVersion;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProtocolInfo)) {
      return false;
    }
    final ProtocolInfo protocolInfo = (ProtocolInfo) o;
    final boolean isProtocolEqual = Objects.equals(this.protocol, protocolInfo.protocol);
    final boolean isProtocolVersionEqual =
        Objects.equals(this.protocolVersion, protocolInfo.protocolVersion);

    return isProtocolEqual && isProtocolVersionEqual;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.protocol, this.protocolVersion);
  }

  public Long getId() {
    return this.id;
  }

  public String getProtocol() {
    return this.protocol;
  }

  public String getProtocolVersion() {
    return this.protocolVersion;
  }
}
