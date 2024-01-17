// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.domain.entities;

import com.google.common.base.Joiner;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;
import java.io.Serial;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.Proxy;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.EventNotificationType;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.LightType;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.LinkType;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.OutputType;

/** An entity class which contains the information of a single device. */
@Entity
@Proxy(lazy = false)
public class Device extends AbstractEntity {

  public static final String PSLD_TYPE = "PSLD";

  public static final String SSLD_TYPE = "SSLD";

  @Serial private static final long serialVersionUID = 7491360758865068487L;

  @Column(unique = true, nullable = false)
  private String deviceUid;

  @Column(unique = true, nullable = false)
  private String deviceIdentification;

  @Column(nullable = false)
  private String ipAddress;

  @Column(nullable = false)
  private String deviceType;

  @Column(nullable = false)
  private boolean lightOn;

  @Column(nullable = true)
  private Integer dimValue;

  @Column(nullable = false)
  private boolean selftestActive;

  @Column() private Integer eventNotifications;

  @Column() private String protocol;

  @Column()
  @Enumerated(EnumType.ORDINAL)
  private LinkType preferredLinkType;

  @Column()
  @Enumerated(EnumType.ORDINAL)
  private LinkType actualLinkType;

  @Column()
  @Enumerated(EnumType.ORDINAL)
  private LightType lightType;

  @Column() private boolean tariffOn;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "device_output_setting", joinColumns = @JoinColumn(name = "device_id"))
  private List<DeviceOutputSetting> outputSettings = new ArrayList<>();

  @Column(nullable = true)
  private Integer sequenceNumber;

  @Column(nullable = true)
  private Integer randomDevice;

  @Column(nullable = true)
  private Integer randomPlatform;

  @Column(nullable = false)
  private boolean hasEveningMorningBurner;

  @Column(nullable = false)
  private String firmwareVersion;

  @Transient private final SecureRandom random = new SecureRandom();

  @Transient private static final Integer SEQUENCE_NUMBER_MAXIMUM = 65535;

  public Device() {
    // Default constructor
  }

  public void setDeviceUid(final byte[] deviceUid) {
    this.deviceUid = Base64.encodeBase64String(deviceUid);
  }

  public String getDeviceUid() {
    return this.deviceUid;
  }

  /** Gets identification of device */
  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  /** Sets identification of device */
  public void setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  /** Gets ip address of device */
  public String getIpAddress() {
    return this.ipAddress;
  }

  /** Sets ip address of device */
  public void setIpAddress(final String ipaddress) {
    this.ipAddress = ipaddress;
  }

  /** Gets type of device */
  public String getDeviceType() {
    return this.deviceType;
  }

  /** Sets type of device */
  public void setDeviceType(final String deviceType) {
    this.deviceType = deviceType;
  }

  public boolean isLightOn() {
    return this.lightOn;
  }

  public void setLightOn(final boolean lightOn) {
    this.lightOn = lightOn;
  }

  public Integer getDimValue() {
    return this.dimValue;
  }

  public void setDimValue(final Integer dimValue) {
    this.dimValue = dimValue;
  }

  public boolean isSelftestActive() {
    return this.selftestActive;
  }

  public void setSelftestActive(final boolean selftestActive) {
    this.selftestActive = selftestActive;
  }

  public void setEventNotifications(final Integer notificationMask) {
    this.eventNotifications = notificationMask;
  }

  public int getEventNotificationMask() {
    // default to 0 when not yet set
    return this.eventNotifications != null ? this.eventNotifications : 0;
  }

  public String getEventNotifications() {
    String returnValue = "";
    final List<EventNotificationType> events = new ArrayList<>();
    if (this.eventNotifications != null && this.eventNotifications > 0) {
      for (final EventNotificationType event : EventNotificationType.values()) {
        if ((this.eventNotifications & (1 << event.ordinal())) != 0) {
          events.add(event);
        }
      }
      if (!events.isEmpty()) {
        returnValue = Joiner.on(",").join(EnumSet.copyOf(events));
      }
    }

    return returnValue;
  }

  public LinkType getPreferredLinkType() {
    return this.preferredLinkType != null ? this.preferredLinkType : LinkType.LINK_NOT_SET;
  }

  public void setPreferredLinkType(final LinkType preferredLinkType) {
    this.preferredLinkType = preferredLinkType;
  }

  public LinkType getActualLinkType() {
    return this.actualLinkType != null ? this.actualLinkType : LinkType.LINK_NOT_SET;
  }

  public void setActualLinkType(final LinkType actualLinkType) {
    this.actualLinkType = actualLinkType;
  }

  public LightType getLightType() {
    return this.lightType != null ? this.lightType : LightType.LT_NOT_SET;
  }

  public void setLightType(final LightType lightType) {
    this.lightType = lightType;
  }

  public boolean isTariffOn() {
    return this.tariffOn;
  }

  public void setTariffOn(final boolean tariffOn) {
    this.tariffOn = tariffOn;
  }

  public List<DeviceOutputSetting> getOutputSettings() {
    if (this.outputSettings == null || this.outputSettings.isEmpty()) {
      return this.createDefaultConfiguration();
    }

    return this.outputSettings;
  }

  public void setOutputSettings(final List<DeviceOutputSetting> outputSettings) {
    this.outputSettings = outputSettings;
  }

  public Integer getSequenceNumber() {
    return this.sequenceNumber;
  }

  public void setSequenceNumber(final Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public Integer getRandomDevice() {
    return this.randomDevice;
  }

  public void setRandomDevice(final Integer randomDevice) {
    this.randomDevice = randomDevice;
  }

  public Integer getRandomPlatform() {
    return this.randomPlatform;
  }

  public void setRandomPlatform(final Integer randomPlatform) {
    this.randomPlatform = randomPlatform;
  }

  public String getProtocol() {
    return this.protocol;
  }

  public void setProtocol(final String protocol) {
    this.protocol = protocol;
  }

  public int doGetNextSequence() {
    int next = this.sequenceNumber + 1;
    if (next > SEQUENCE_NUMBER_MAXIMUM) {
      next = 0;
    }

    return next;
  }

  /**
   * Generate a secure random number within range 0 to 65535.
   *
   * @return The random number.
   */
  public Integer doGenerateRandomNumber() {
    return this.random.nextInt(SEQUENCE_NUMBER_MAXIMUM + 1);
  }

  /**
   * Create default configuration for a device (based on type).
   *
   * @return default configuration
   */
  private List<DeviceOutputSetting> createDefaultConfiguration() {
    final List<DeviceOutputSetting> defaultConfiguration = new ArrayList<>();

    if (this.deviceType.equalsIgnoreCase(SSLD_TYPE)) {
      defaultConfiguration.add(new DeviceOutputSetting(1, 1, OutputType.TARIFF));
      defaultConfiguration.add(new DeviceOutputSetting(2, 2, OutputType.LIGHT));
      defaultConfiguration.add(new DeviceOutputSetting(3, 3, OutputType.LIGHT));
    }

    return defaultConfiguration;
  }

  public boolean getHasEveningMorningBurner() {
    return this.hasEveningMorningBurner;
  }

  public String getFirmwareVersion() {
    return this.firmwareVersion;
  }

  public void setFirmwareVersion(final String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }
}
