/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.domain.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.OutputType;

@Embeddable
public class DeviceOutputSetting implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6222359279910235018L;

  @Column private int internalId;

  @Column private int externalId;

  @Column
  @Enumerated(EnumType.ORDINAL)
  private OutputType outputType;

  /** Default constructor. */
  @SuppressWarnings("unused")
  private DeviceOutputSetting() {
    // Default constructor needed for Hibernate.
  }

  public DeviceOutputSetting(
      final int internalId, final int externalId, final OutputType outputType) {
    this.internalId = internalId;
    this.externalId = externalId;
    this.outputType = outputType;
  }

  public int getInternalId() {
    return this.internalId;
  }

  public int getExternalId() {
    return this.externalId;
  }

  public OutputType getOutputType() {
    return this.outputType;
  }
}
