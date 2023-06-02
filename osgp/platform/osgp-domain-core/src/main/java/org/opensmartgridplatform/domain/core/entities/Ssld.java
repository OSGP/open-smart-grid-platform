//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.entities;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Ssld extends Device {
  /** Serial Version UID. */
  private static final long serialVersionUID = 1492794916633445439L;

  /** Device type indicator for PSLD */
  public static final String PSLD_TYPE = "PSLD";

  /** Device type indicator for SSLD */
  public static final String SSLD_TYPE = "SSLD";

  @Column private boolean hasPublicKey;

  @Column private boolean hasSchedule;

  @OneToMany(
      mappedBy = "device",
      targetEntity = Ean.class,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.FALSE)
  private final List<Ean> eans = new ArrayList<>();

  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection()
  @CollectionTable(name = "device_output_setting", joinColumns = @JoinColumn(name = "device_id"))
  private List<DeviceOutputSetting> outputSettings = new ArrayList<>();

  @OneToMany(
      mappedBy = "device",
      cascade = {CascadeType.MERGE, CascadeType.PERSIST},
      orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.FALSE)
  @OrderBy("index ASC")
  private List<RelayStatus> relayStatuses = new ArrayList<>();

  @ManyToOne(
      optional = true,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST},
      fetch = FetchType.LAZY)
  @JoinColumn(name = "light_measurement_device_id")
  private LightMeasurementDevice lightMeasurementDevice;

  public Ssld() {
    // Default constructor.
  }

  public Ssld(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public Ssld(
      final String deviceIdentification,
      final String deviceType,
      final InetAddress networkAddress,
      final boolean activated,
      final boolean hasSchedule) {
    this.deviceIdentification = deviceIdentification;
    this.deviceType = deviceType;
    this.networkAddress = networkAddress;
    this.isActivated = activated;
    this.hasSchedule = hasSchedule;
  }

  public Ssld(
      final String deviceIdentification,
      final String alias,
      final Address containerAddress,
      final GpsCoordinates gpsCoordinates,
      final CdmaSettings cdmaSettings) {
    super(deviceIdentification, alias, containerAddress, gpsCoordinates, cdmaSettings);
  }

  @Override
  public boolean equals(final Object o) {
    /*
     * Code quality checks indicate that equals should be overridden because
     * this class extends Device (which overrides equals) and adds fields.
     *
     * The equals implementation of Device however is perfectly OK, since it
     * is based on the deviceIdentification, which is a natural key for
     * Device as well as its subclasses.
     *
     * So, just call super here.
     */
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    /*
     * Override hashCode, because equals is overridden as well. This should
     * get rid of a reported bug by the code quality checks, but the super
     * implementation is just fine here, like with equals.
     */
    return super.hashCode();
  }

  public boolean isPublicKeyPresent() {
    return this.hasPublicKey;
  }

  public void setPublicKeyPresent(final boolean isPublicKeyPresent) {
    this.hasPublicKey = isPublicKeyPresent;
  }

  public void setHasSchedule(final boolean hasSchedule) {
    this.hasSchedule = hasSchedule;
  }

  public boolean getHasSchedule() {
    return this.hasSchedule;
  }

  /**
   * Get the Ean codes for this device.
   *
   * @return List of Ean codes for this device.
   */
  public List<Ean> getEans() {
    return this.eans;
  }

  public void setEans(final List<Ean> eans) {
    this.eans.clear();
    if (eans != null) {
      this.eans.addAll(eans);
    }
  }

  public void addEan(final Ean ean) {
    this.eans.add(ean);
  }

  public List<DeviceOutputSetting> getOutputSettings() {
    if (this.outputSettings == null || this.outputSettings.isEmpty()) {
      return Collections.unmodifiableList(this.createDefaultConfiguration());
    }

    return Collections.unmodifiableList(this.outputSettings);
  }

  /**
   * Returns the {@link DeviceOutputSetting} for the given external index, or null if it doesn't
   * exist.
   */
  public DeviceOutputSetting getOutputSetting(final int externalId) {
    for (final DeviceOutputSetting setting : this.outputSettings) {
      if (setting.getExternalId() == externalId) {
        return setting;
      }
    }

    return null;
  }

  public void updateOutputSettings(final List<DeviceOutputSetting> outputSettings) {
    this.outputSettings = outputSettings;
  }

  public List<DeviceOutputSetting> receiveOutputSettings() {
    return this.outputSettings;
  }

  public List<RelayStatus> getRelayStatuses() {
    return this.relayStatuses;
  }

  public void setRelayStatuses(final List<RelayStatus> relayStatuses) {
    if (relayStatuses == null) {
      this.relayStatuses = new ArrayList<>();
    } else {
      this.relayStatuses = relayStatuses;
    }
  }

  /**
   * Updates the {@link RelayStatus} for the given index if it exists. If a status doesn't exist
   * yet, it is created.
   *
   * @param relayStatus The status for the relay to add/update.
   */
  public void addOrUpdateRelayStatus(final RelayStatus relayStatus) {
    final RelayStatus currentRelayStatus = this.getRelayStatusByIndex(relayStatus.getIndex());
    if (currentRelayStatus == null) {
      this.relayStatuses.add(relayStatus);
    } else {
      currentRelayStatus.updateLastKnownState(
          relayStatus.isLastKnownState(), relayStatus.getLastKnownStateTime());
      currentRelayStatus.updateLastSwitchingEventState(
          relayStatus.isLastSwitchingEventState(), relayStatus.getLastSwitchingEventTime());
    }
  }

  /** Returns the {@link RelayStatus} for the given index, or null if it doesn't exist. */
  public RelayStatus getRelayStatusByIndex(final int index) {
    for (final RelayStatus r : this.relayStatuses) {
      if (r.getIndex() == index) {
        return r;
      }
    }

    return null;
  }

  public void setLightMeasurementDevice(final LightMeasurementDevice lightMeasurementDevice) {
    this.lightMeasurementDevice = lightMeasurementDevice;
  }

  public LightMeasurementDevice getLightMeasurementDevice() {
    return this.lightMeasurementDevice;
  }

  /**
   * Updates the {@link RelayStatus} of the last switching event for the indexes in relayStatusBy
   * Index. If there's no status for a certain index yet, it is created.
   *
   * @param relayStatusByIndex The status per index for the device.
   */
  public void updateSwitchingEventRelayStatuses(
      final Map<Integer, RelayStatus> relayStatusByIndex) {
    if (this.relayStatuses == null) {
      this.relayStatuses = new ArrayList<>();
    }
    final Map<Integer, RelayStatus> unhandledStatusesByIndex = new TreeMap<>(relayStatusByIndex);
    for (final RelayStatus r : this.relayStatuses) {
      final RelayStatus newStatus = unhandledStatusesByIndex.remove(r.getIndex());
      if (newStatus != null
          && (r.getLastSwitchingEventTime() == null
              || newStatus.getLastSwitchingEventTime().after(r.getLastSwitchingEventTime()))) {
        r.updateLastSwitchingEventState(
            newStatus.isLastSwitchingEventState(), newStatus.getLastSwitchingEventTime());
      }
    }
    this.relayStatuses.addAll(unhandledStatusesByIndex.values());
  }

  /**
   * Create default configuration for a device (based on type).
   *
   * @return default configuration
   */
  private List<DeviceOutputSetting> createDefaultConfiguration() {
    final List<DeviceOutputSetting> defaultConfiguration = new ArrayList<>();

    if (this.deviceType == null) {
      return defaultConfiguration;
    }

    if (this.deviceType.equalsIgnoreCase(SSLD_TYPE)) {
      defaultConfiguration.add(new DeviceOutputSetting(1, 1, RelayType.TARIFF, ""));
      defaultConfiguration.add(new DeviceOutputSetting(2, 2, RelayType.LIGHT, ""));
      defaultConfiguration.add(new DeviceOutputSetting(3, 3, RelayType.LIGHT, ""));

      return defaultConfiguration;
    }

    if (this.deviceType.equalsIgnoreCase(PSLD_TYPE)) {
      defaultConfiguration.add(new DeviceOutputSetting(1, 1, RelayType.LIGHT, ""));
      return defaultConfiguration;
    }

    return defaultConfiguration;
  }
}
