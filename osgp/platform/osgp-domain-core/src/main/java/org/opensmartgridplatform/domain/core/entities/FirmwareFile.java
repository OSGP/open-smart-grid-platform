// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import org.hibernate.annotations.SortNatural;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

/**
 * FirmwareFile entity class holds information about the device model or type.
 *
 * <p>A FirmwareFile can be available for multiple device models, and a device model can be a match
 * for multiple firmware files.
 *
 * <p>A FirmwareFile is uniquely defined by a DeviceModel and the combination of module versions for
 * the firmware modules in the file. A unique identification is introduced to be able to reference a
 * FirmwareFile between separate OSGP components.
 */
@Entity
public class FirmwareFile extends AbstractEntity {

  private static final long serialVersionUID = 2L;

  @Column(unique = true, nullable = false, updatable = false)
  private String identification;

  @ManyToMany(
      cascade = {CascadeType.PERSIST, CascadeType.MERGE},
      fetch = FetchType.EAGER)
  @JoinTable(
      name = "device_model_firmware_file",
      joinColumns = @JoinColumn(name = "firmware_file_id"),
      inverseJoinColumns = @JoinColumn(name = "device_model_id"))
  @OrderBy("modelCode")
  @SortNatural
  private final SortedSet<DeviceModel> deviceModels = new TreeSet<>();

  @OneToMany(
      mappedBy = "firmwareFile",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private final Set<FirmwareFileFirmwareModule> firmwareModules = new HashSet<>();

  @Column private String filename;

  @Column(length = 100)
  private String description;

  @Column private boolean pushToNewDevices;

  @Lob @Column private byte[] file;

  @Column private String hash;

  @Column private boolean active;

  @Column private byte[] imageIdentifier;

  protected FirmwareFile() {
    // Default constructor
  }

  private FirmwareFile(final Builder builder) {
    this.identification = builder.identification;
    this.filename = builder.filename;
    this.description = builder.description;
    this.pushToNewDevices = builder.pushToNewDevices;
    this.file = builder.file;
    this.hash = builder.hash;
    this.active = builder.active;
    this.imageIdentifier = builder.imageIdentifier;
  }

  public void updateFirmwareModuleData(final Map<FirmwareModule, String> versionsByModule) {
    this.firmwareModules.clear();
    versionsByModule.forEach(this::addFirmwareModule);
  }

  public void updateFirmwareDeviceModels(final List<DeviceModel> deviceModels) {
    this.deviceModels.clear();
    deviceModels.forEach(this::addDeviceModel);
  }

  public String getIdentification() {
    return this.identification;
  }

  public byte[] getImageIdentifier() {
    return this.imageIdentifier;
  }

  public SortedSet<DeviceModel> getDeviceModels() {
    return this.deviceModels;
  }

  public void addDeviceModel(final DeviceModel deviceModel) {
    this.deviceModels.add(deviceModel);
  }

  public void removeDeviceModel(final DeviceModel deviceModel) {
    this.deviceModels.remove(deviceModel);
  }

  /**
   * Returns an unmodifiable map of versions by firmware module.
   *
   * <p>To alter the module versions with this firmware file, use {@link
   * #addFirmwareModule(FirmwareModule, String)} and/or {@link
   * #removeFirmwareModule(FirmwareModule)}. To prevent unexpected situations firmware module
   * versions should probably be configured as close as possible to the registration of this
   * firmware file, and be left as such from that time on.
   */
  public Map<FirmwareModule, String> getModuleVersions() {
    final Map<FirmwareModule, String> moduleVersions = new TreeMap<>();
    for (final FirmwareFileFirmwareModule firmwareModule : this.firmwareModules) {
      moduleVersions.put(firmwareModule.getFirmwareModule(), firmwareModule.getModuleVersion());
    }
    return Collections.unmodifiableMap(moduleVersions);
  }

  /**
   * Registers the version for the given module as included in this firmware file.
   *
   * <p><strong>NB</strong> Registering firmware module versions should only happen when the
   * firmware file itself is registered. Modifications later-on will probably not be expected and
   * could lead to unpredictable results.
   *
   * @param firmwareModule the firmware module for which a version should be added as included with
   *     this firmware file.
   * @param moduleVersion the version of the specific {@code firmwareModule} in this firmware file.
   * @throws IllegalArgumentException if this firmware file already has a version of the {@code
   *     firmwareModule}.
   */
  public void addFirmwareModule(final FirmwareModule firmwareModule, final String moduleVersion) {
    final FirmwareFileFirmwareModule firmwareFileFirmwareModule =
        new FirmwareFileFirmwareModule(this, firmwareModule, moduleVersion);
    /*
     * Equals for FirmwareFileFirmwareModule is based on FirmwareFile and
     * FirmwareModule only, so even for a different module version the
     * following call would return true.
     */
    if (this.firmwareModules.contains(firmwareFileFirmwareModule)) {
      throw new IllegalArgumentException(
          "FirmwareFile already has a module version for " + firmwareModule.getDescription());
    }
    this.firmwareModules.add(firmwareFileFirmwareModule);
  }

  /**
   * Unregisters the version for the given module if it was previously registered as included in
   * this firmware file.
   *
   * <p><strong>NB</strong> Unregistering firmware module versions should probably never happen,
   * since versions are expected to be properly registered when the firmware file itself is
   * registered. Modifications later-on will probably not be expected and could lead to
   * unpredictable results.
   *
   * @param firmwareModule the firmware module for which no version should be registered as included
   *     with this firmware file any longer.
   */
  public void removeFirmwareModule(final FirmwareModule firmwareModule) {
    for (final Iterator<FirmwareFileFirmwareModule> iterator = this.firmwareModules.iterator();
        iterator.hasNext(); ) {
      final FirmwareFileFirmwareModule firmwareFileFirmwareModule = iterator.next();
      if (firmwareFileFirmwareModule.getFirmwareFile().equals(this)
          && firmwareFileFirmwareModule.getFirmwareModule().equals(firmwareModule)) {
        iterator.remove();
        firmwareFileFirmwareModule.prepareForRemoval();
      }
    }
  }

  public String getFilename() {
    return this.filename;
  }

  public String getDescription() {
    return this.description;
  }

  public boolean getPushToNewDevices() {
    return this.pushToNewDevices;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(final boolean active) {
    this.active = active;
  }

  /**
   * @deprecated Different types of modules can vary over time when new types of devices are added
   *     to the platform. Use the more general {@link #getModuleVersions()} instead.
   */
  @Deprecated
  public String getModuleVersionComm() {
    final Map<FirmwareModule, String> moduleVersions = this.getModuleVersions();
    for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
      final FirmwareModule firmwareModule = moduleVersion.getKey();
      if (FirmwareModuleData.MODULE_DESCRIPTION_COMM.equals(firmwareModule.getDescription())) {
        return moduleVersion.getValue();
      }
    }
    return null;
  }

  /**
   * @deprecated Different types of modules can vary over time when new types of devices are added
   *     to the platform. Use the more general {@link #getModuleVersions()} instead.
   */
  @Deprecated
  public String getModuleVersionFunc() {
    final Map<FirmwareModule, String> moduleVersions = this.getModuleVersions();
    /*
     * This firmware module version may have been mapped to the '
     * functional' module version, or - for smart meters - to the
     * 'active_firmware' module version. If there is no value for the
     * 'functional' module version, return the 'active_firmware' module
     * version (or null if neither is present).
     */
    String activeFirmwareVersion = null;
    for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
      final FirmwareModule firmwareModule = moduleVersion.getKey();
      if (FirmwareModuleData.MODULE_DESCRIPTION_FUNC.equals(firmwareModule.getDescription())) {
        return moduleVersion.getValue();
      } else if (FirmwareModuleData.MODULE_DESCRIPTION_FUNC_SMART_METERING.equals(
          firmwareModule.getDescription())) {
        activeFirmwareVersion = moduleVersion.getValue();
      }
    }
    return activeFirmwareVersion;
  }

  /**
   * @deprecated Different types of modules can vary over time when new types of devices are added
   *     to the platform. Use the more general {@link #getModuleVersions()} instead.
   */
  @Deprecated
  public String getModuleVersionSec() {
    final Map<FirmwareModule, String> moduleVersions = this.getModuleVersions();
    for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
      final FirmwareModule firmwareModule = moduleVersion.getKey();
      if (FirmwareModuleData.MODULE_DESCRIPTION_SEC.equals(firmwareModule.getDescription())) {
        return moduleVersion.getValue();
      }
    }
    return null;
  }

  /**
   * @deprecated Different types of modules can vary over time when new types of devices are added
   *     to the platform. Use the more general {@link #getModuleVersions()} instead.
   */
  @Deprecated
  public String getModuleVersionMa() {
    final Map<FirmwareModule, String> moduleVersions = this.getModuleVersions();
    for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
      final FirmwareModule firmwareModule = moduleVersion.getKey();
      if (FirmwareModuleData.MODULE_DESCRIPTION_MA.equals(firmwareModule.getDescription())) {
        return moduleVersion.getValue();
      }
    }
    return null;
  }

  /**
   * @deprecated Different types of modules can vary over time when new types of devices are added
   *     to the platform. Use the more general {@link #getModuleVersions()} instead.
   */
  @Deprecated
  public String getModuleVersionMbus() {
    final Map<FirmwareModule, String> moduleVersions = this.getModuleVersions();
    for (final Entry<FirmwareModule, String> moduleVersion : moduleVersions.entrySet()) {
      final FirmwareModule firmwareModule = moduleVersion.getKey();
      if (FirmwareModuleData.MODULE_DESCRIPTION_MBUS.equals(firmwareModule.getDescription())) {
        return moduleVersion.getValue();
      }
    }
    return null;
  }

  public void setFilename(final String filename) {
    this.filename = filename;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setPushToNewDevices(final boolean pushToNewDevices) {
    this.pushToNewDevices = pushToNewDevices;
  }

  public void setImageIdentifier(final byte[] imageIdentifier) {
    this.imageIdentifier = imageIdentifier;
  }

  public String getHash() {
    return this.hash;
  }

  public void setHash(final String hash) {
    this.hash = hash;
  }

  @Lob
  @Basic(fetch = FetchType.LAZY)
  public byte[] getFile() {
    return this.file;
  }

  public void setFile(final byte[] file) {
    this.file = file;
  }

  @Override
  public String toString() {
    return "FirmwareFile [identification="
        + this.identification
        + ", filename="
        + this.filename
        + ", description="
        + this.description
        + ", pushToNewDevices="
        + this.pushToNewDevices
        + ", file="
        + Arrays.toString(this.file)
        + ", imageIdentifier="
        + Arrays.toString(this.imageIdentifier)
        + ", hash="
        + this.hash
        + "]";
  }

  public static class Builder {

    private String identification = UUID.randomUUID().toString().replace("-", "");
    private String filename;
    private String description;
    private boolean pushToNewDevices;
    private byte[] file;
    private String hash;
    private boolean active;
    private byte[] imageIdentifier;

    public Builder withIdentification(final String identification) {
      if (identification != null) {
        this.identification = identification;
      }
      return this;
    }

    public Builder withFilename(final String filename) {
      this.filename = filename;
      return this;
    }

    public Builder withDescription(final String description) {
      this.description = description;
      return this;
    }

    public Builder withPushToNewDevices(final boolean pushToNewDevices) {
      this.pushToNewDevices = pushToNewDevices;
      return this;
    }

    public Builder withFile(final byte[] file) {
      this.file = file;
      return this;
    }

    public Builder withImageIdentifier(final byte[] imageIdentifier) {
      this.imageIdentifier = imageIdentifier;
      return this;
    }

    public Builder withHash(final String hash) {
      this.hash = hash;
      return this;
    }

    public Builder withActive(final boolean active) {
      this.active = active;
      return this;
    }

    public FirmwareFile build() {
      return new FirmwareFile(this);
    }
  }
}
