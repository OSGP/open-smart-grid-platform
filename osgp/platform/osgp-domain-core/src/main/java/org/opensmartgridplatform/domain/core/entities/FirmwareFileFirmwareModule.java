// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

/**
 * FirmwareFileFirmwareModule entity class holds the module version of a firmware module included in
 * a firmware file.
 */
@Entity
public class FirmwareFileFirmwareModule
    implements Comparable<FirmwareFileFirmwareModule>, Serializable {

  private static final long serialVersionUID = 1301961968551172326L;

  @Embeddable
  public static class FirmwareFileFirmwareModuleId implements Serializable {

    private static final long serialVersionUID = 6612499807879722629L;

    @Column private Long firmwareFileId;

    @Column private Long firmwareModuleId;

    protected FirmwareFileFirmwareModuleId() {
      // No-argument constructor, not to be used by application code.
    }

    public FirmwareFileFirmwareModuleId(final Long firmwareFileId, final Long firmwareModuleId) {
      this.firmwareFileId = firmwareFileId;
      this.firmwareModuleId = firmwareModuleId;
    }

    public Long getFirmwareFileId() {
      return this.firmwareFileId;
    }

    public Long getFirmwareModuleId() {
      return this.firmwareModuleId;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof FirmwareFileFirmwareModuleId)) {
        return false;
      }
      final FirmwareFileFirmwareModuleId other = (FirmwareFileFirmwareModuleId) obj;
      return Objects.equals(this.firmwareFileId, other.firmwareFileId)
          && Objects.equals(this.firmwareModuleId, other.firmwareModuleId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.firmwareFileId, this.firmwareModuleId);
    }

    @Override
    public String toString() {
      return String.format("[file=%s, module=%s]", this.firmwareFileId, this.firmwareModuleId);
    }
  }

  @EmbeddedId private FirmwareFileFirmwareModuleId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "firmware_file_id")
  @MapsId("firmwareFileId")
  private FirmwareFile firmwareFile;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "firmware_module_id")
  @MapsId("firmwareModuleId")
  private FirmwareModule firmwareModule;

  @Column(nullable = false)
  private String moduleVersion;

  protected FirmwareFileFirmwareModule() {
    // No-argument constructor, not to be used by application code.
  }

  public FirmwareFileFirmwareModule(
      final FirmwareFile firmwareFile,
      final FirmwareModule firmwareModule,
      final String moduleVersion) {
    this.firmwareFile = firmwareFile;
    this.firmwareModule = firmwareModule;
    this.id = new FirmwareFileFirmwareModuleId(firmwareFile.getId(), firmwareModule.getId());
    this.moduleVersion = moduleVersion;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof FirmwareFileFirmwareModule)) {
      return false;
    }
    final FirmwareFileFirmwareModule other = (FirmwareFileFirmwareModule) obj;
    return Objects.equals(this.firmwareFile, other.firmwareFile)
        && Objects.equals(this.firmwareModule, other.firmwareModule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.firmwareFile, this.firmwareModule);
  }

  @Override
  public int compareTo(final FirmwareFileFirmwareModule o) {
    final int compareFirmwareFile =
        this.firmwareFile.getIdentification().compareTo(o.firmwareFile.getIdentification());
    if (compareFirmwareFile != 0) {
      return compareFirmwareFile;
    }
    final int compareFirmwareModule = this.firmwareModule.compareTo(o.firmwareModule);
    if (compareFirmwareModule != 0) {
      return compareFirmwareModule;
    }
    return this.moduleVersion.compareTo(o.moduleVersion);
  }

  @Override
  public String toString() {
    return String.format(
        "FirmwareFileFirmwareModule[file=%s, module=%s, version=%s]",
        this.firmwareFile.getIdentification(),
        this.firmwareModule.getDescription(),
        this.moduleVersion);
  }

  public void prepareForRemoval() {
    this.firmwareFile = null;
    this.firmwareModule = null;
  }

  public FirmwareFileFirmwareModuleId getId() {
    return this.id;
  }

  public FirmwareFile getFirmwareFile() {
    return this.firmwareFile;
  }

  public FirmwareModule getFirmwareModule() {
    return this.firmwareModule;
  }

  public String getModuleVersion() {
    return this.moduleVersion;
  }

  public void setModuleVersion(final String moduleVersion) {
    this.moduleVersion = moduleVersion;
  }
}
