// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.domain.entities;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

/** Abstract base class for entities. */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

  // === FIELDS [START] ===

  /** Serial Version UID. */
  private static final long serialVersionUID = 1303732164728920566L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  protected Long id;

  @Column(nullable = false)
  protected Instant creationTime = Instant.now();

  @Column(nullable = false)
  protected Instant modificationTime = Instant.now();

  @Version private Long version = -1L;

  // === FIELDS [END] ===

  // === CTOR [START] ===

  // === CTOR [END] ===

  // === GETTERS & SETTERS [START] ===

  /**
   * @return the id
   */
  public Long getId() {
    return this.id;
  }

  /**
   * @return the creation time
   */
  public Instant getCreationTime() {
    return this.creationTime;
  }

  /**
   * @return the creation time
   */
  public Instant getCreationTimeInstant() {
    return this.creationTime;
  }

  /**
   * @return the modification time
   */
  public Instant getModificationTime() {
    return this.modificationTime;
  }

  /**
   * @return the modification time
   */
  public Instant getModificationTimeInstant() {
    return this.modificationTime;
  }

  /**
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long newVersion) {
    this.version = newVersion;
  }

  // === GETTERS & SETTERS [END] ===

  // === METHODS [START] ===

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

  // === METHODS [END] ===
}
