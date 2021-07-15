/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.db.api.iec61850.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.opensmartgridplatform.core.db.api.iec61850valueobjects.RelayFunction;
import org.opensmartgridplatform.core.db.api.iec61850valueobjects.RelayType;

/** Copy of the platform DeviceOutputSetting class */
@Embeddable
public class DeviceOutputSetting implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -617569213968100631L;

  @Column private int internalId;

  @Column private int externalId;

  @Column private String alias;

  @Column(name = "output_type", length = 25)
  private RelayType relayType;

  @Column private RelayFunction relayFunction;

  public String getAlias() {
    return this.alias;
  }

  public RelayFunction getRelayFunction() {
    return this.relayFunction;
  }

  public DeviceOutputSetting() {
    // Default constructor
  }

  public DeviceOutputSetting(
      final int internalId, final int externalId, final RelayType relayType, final String alias) {
    this.internalId = internalId;
    this.externalId = externalId;
    this.alias = alias;
    this.relayType = relayType;
  }

  public DeviceOutputSetting(
      final int internalId, final int externalId, final RelayType relayType) {
    this.internalId = internalId;
    this.externalId = externalId;
    this.relayType = relayType;
  }

  public int getInternalId() {
    return this.internalId;
  }

  public int getExternalId() {
    return this.externalId;
  }

  public RelayType getRelayType() {
    return this.relayType;
  }
}
