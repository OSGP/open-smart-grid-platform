// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.db.api.iec61850.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/** Stripped down version of the platform Device class */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Device implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -4119222173415540822L;

  /** Primary key. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  protected Long id;

  /** Creation time of this entity. This field is set by { @see this.prePersist() }. */
  @Column(nullable = false)
  protected Instant creationTime = Instant.now();

  /** Modification time of this entity. This field is set by { @see this.preUpdate() }. */
  @Column(nullable = false)
  protected Instant modificationTime = Instant.now();

  /** Version of this entity. */
  @Version private Long version = -1L;

  /** Device identification of a device. This is the main value used to find a device. */
  @Column(unique = true, nullable = false, length = 40)
  protected String deviceIdentification;

  /** GPS latitude. */
  @Column private Float gpsLatitude;

  /** GPS longitude. */
  @Column private Float gpsLongitude;

  /** Indicates the type of the device. Example { @see Ssld.SSLD_TYPE } */
  @Column() protected String deviceType;

  /** Protocol information indicates which protocol this device is using. */
  @ManyToOne()
  @JoinColumn(name = "protocol_info_id")
  protected ProtocolInfo protocolInfo;

  public Device() {
    // Default constructor
  }

  public Device(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final Device device = (Device) o;
    return Objects.equals(this.deviceIdentification, device.deviceIdentification);
  }

  public Instant getCreationTime() {
    return this.creationTime;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public Float getGpsLatitude() {
    return this.gpsLatitude;
  }

  public Float getGpsLongitude() {
    return this.gpsLongitude;
  }

  public Long getId() {
    return this.id;
  }

  public Instant getModificationTime() {
    return this.modificationTime;
  }

  public Long getVersion() {
    return this.version;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.deviceIdentification);
  }

  /** Method for actions to be taken before inserting. */
  @PrePersist
  private void prePersist() {
    final Instant now = Instant.now();
    this.creationTime = now;
    this.modificationTime = now;
  }

  /** Method for actions to be taken before updating. */
  @PreUpdate
  private void preUpdate() {
    this.modificationTime = Instant.now();
  }

  public void setVersion(final Long newVersion) {
    this.version = newVersion;
  }

  /**
   * This setter is only needed for testing. Don't use this in production code.
   *
   * @param id The id.
   */
  public void setId(final Long id) {
    this.id = id;
  }
}
