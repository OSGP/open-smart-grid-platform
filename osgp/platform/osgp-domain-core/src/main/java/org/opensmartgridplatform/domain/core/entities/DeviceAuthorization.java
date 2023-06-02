//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class DeviceAuthorization extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = 1468328289658974067L;

  @ManyToOne(optional = false)
  @JoinColumn(name = "device")
  @Cascade(
      value = {
        CascadeType.MERGE,
        CascadeType.PERSIST,
        CascadeType.REFRESH,
        CascadeType.SAVE_UPDATE
      })
  private Device device;

  @ManyToOne(optional = false)
  @JoinColumn(name = "organisation")
  private Organisation organisation;

  @Column(nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private DeviceFunctionGroup functionGroup;

  public DeviceAuthorization() {
    // Default constructor
  }

  public DeviceAuthorization(
      final Device device,
      final Organisation organisation,
      final DeviceFunctionGroup functionGroup) {
    this.device = device;
    this.organisation = organisation;
    this.functionGroup = functionGroup;
  }

  public Device getDevice() {
    return this.device;
  }

  public Organisation getOrganisation() {
    return this.organisation;
  }

  public DeviceFunctionGroup getFunctionGroup() {
    return this.functionGroup;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DeviceAuthorization)) {
      return false;
    }
    final DeviceAuthorization authorization = (DeviceAuthorization) o;
    // Only comparing the device and organisation identifications (and not
    // the complete objects) to prevent stack overflow errors when comparing
    // devices (which contain device authorizations).
    final boolean isDeviceEqual = Objects.equals(this.device, authorization.device);
    final boolean isOrganisationEqual =
        Objects.equals(this.organisation, authorization.organisation);
    final boolean isDeviceFunctionGroupEqual =
        Objects.equals(this.functionGroup, authorization.functionGroup);

    return isDeviceEqual && isOrganisationEqual && isDeviceFunctionGroupEqual;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.device, this.organisation, this.functionGroup.name());
  }
}
