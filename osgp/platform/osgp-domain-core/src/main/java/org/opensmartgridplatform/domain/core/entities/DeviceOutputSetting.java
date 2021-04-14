/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.opensmartgridplatform.domain.core.valueobjects.RelayFunction;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;

@Embeddable
public class DeviceOutputSetting implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -617569053968100631L;

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

  public RelayType getOutputType() {
    return this.relayType;
  }

  public void setAlias(final String alias) {
    this.alias = alias;
  }
}
