/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.db.api.iec61850.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
