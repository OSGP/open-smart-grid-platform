// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

/** DeviceModel entity class holds information about the device model or type */
@Entity
public class DeviceModel extends AbstractEntity implements Comparable<DeviceModel> {

  private static final long serialVersionUID = 7957241305474770350L;

  @ManyToOne()
  @JoinColumn(name = "manufacturer_id")
  private Manufacturer manufacturer;

  @Column(nullable = false, length = 255)
  private String modelCode;

  @Column(length = 255)
  private String description;

  @Column private boolean fileStorage;

  public DeviceModel() {
    // Default constructor
  }

  public DeviceModel(
      final Manufacturer manufacturer, final String modelCode, final String description) {
    this(manufacturer, modelCode, description, true);
  }

  public DeviceModel(
      final Manufacturer manufacturer,
      final String modelCode,
      final String description,
      final boolean fileStorage) {
    this.manufacturer = manufacturer;
    this.modelCode = modelCode;
    this.description = description;
    this.fileStorage = fileStorage;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DeviceModel)) {
      return false;
    }
    final DeviceModel other = (DeviceModel) obj;
    return Objects.equals(this.modelCode, other.modelCode)
        && Objects.equals(this.manufacturer, other.manufacturer);
  }

  @Override
  public int compareTo(final DeviceModel o) {
    final int compareManufacturer = this.manufacturer.compareTo(o.manufacturer);
    if (compareManufacturer != 0) {
      return compareManufacturer;
    }
    return this.modelCode.compareTo(o.modelCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.modelCode, this.manufacturer);
  }

  @Override
  public String toString() {
    return String.format(
        "DeviceModel[manufacturer=%s, code=%s]", this.manufacturer.getCode(), this.modelCode);
  }

  public Manufacturer getManufacturer() {
    return this.manufacturer;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getModelCode() {
    return this.modelCode;
  }

  public boolean isFileStorage() {
    return this.fileStorage;
  }
}
