// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.db.api.iec61850.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.opensmartgridplatform.core.db.api.iec61850valueobjects.RelayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Copy of the platform Ssld class */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Ssld extends Device {

  private static final Logger LOGGER = LoggerFactory.getLogger(Ssld.class);

  /** Serial Version UID. */
  private static final long serialVersionUID = 1492214916633445439L;

  /** Device type indicator for PSLD */
  public static final String PSLD_TYPE = "PSLD";

  /** Device type indicator for SSLD */
  public static final String SSLD_TYPE = "SSLD";

  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection()
  @CollectionTable(name = "device_output_setting", joinColumns = @JoinColumn(name = "device_id"))
  private List<DeviceOutputSetting> outputSettings = new ArrayList<>();

  public Ssld() {
    // Default constructor.
  }

  public Ssld(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public List<DeviceOutputSetting> getOutputSettings() {
    if (this.outputSettings == null || this.outputSettings.isEmpty()) {
      return Collections.unmodifiableList(this.createDefaultConfiguration());
    }

    return Collections.unmodifiableList(this.outputSettings);
  }

  public void updateOutputSettings(final List<DeviceOutputSetting> outputSettings) {
    this.outputSettings = outputSettings;
  }

  public List<DeviceOutputSetting> receiveOutputSettings() {
    return this.outputSettings;
  }

  /*
   * Create default configuration for a device (based on type).
   *
   * @return default configuration
   */
  private List<DeviceOutputSetting> createDefaultConfiguration() {
    final List<DeviceOutputSetting> defaultConfiguration = new ArrayList<>();

    if (this.deviceType == null) {
      LOGGER.warn("DeviceType is null, using empty list of DeviceOutputSetting");
      return defaultConfiguration;
    }

    if (this.deviceType.equalsIgnoreCase(SSLD_TYPE)) {
      defaultConfiguration.add(new DeviceOutputSetting(1, 1, RelayType.TARIFF, ""));
      defaultConfiguration.add(new DeviceOutputSetting(2, 2, RelayType.LIGHT, ""));
      defaultConfiguration.add(new DeviceOutputSetting(3, 3, RelayType.LIGHT, ""));
      defaultConfiguration.add(new DeviceOutputSetting(4, 4, RelayType.LIGHT, ""));

      LOGGER.warn(
          "DeviceType is SSLD, returning default list of DeviceOutputSetting: 1 TARIFF, 2 & 3 & 4 LIGHT");
      return defaultConfiguration;
    }

    if (this.deviceType.equalsIgnoreCase(PSLD_TYPE)) {
      defaultConfiguration.add(new DeviceOutputSetting(1, 1, RelayType.LIGHT, ""));
      LOGGER.warn("DeviceType is PSLD, returning default list of DeviceOutputSetting: 1 LIGHT");
      return defaultConfiguration;
    }

    return defaultConfiguration;
  }
}
