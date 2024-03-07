// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
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
