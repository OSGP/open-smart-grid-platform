//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Table(name = "iec60870_device")
public class Iec60870Device extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = -8793285954115476857L;

  @Column(unique = true, nullable = false, length = 40)
  private String deviceIdentification;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private DeviceType deviceType;

  @Column private String gatewayDeviceIdentification;

  @Column(nullable = false)
  private Integer commonAddress;

  @Column private Integer port;

  @Column private Integer informationObjectAddress;

  public Iec60870Device() {
    // Default constructor for Hibernate
  }

  public Iec60870Device(final String deviceIdentification) {
    this(deviceIdentification, DeviceType.DISTRIBUTION_AUTOMATION_DEVICE);
  }

  public Iec60870Device(final String deviceIdentification, final DeviceType deviceType) {
    this.deviceIdentification = deviceIdentification;
    this.deviceType = deviceType;
  }

  @Override
  public String toString() {
    return String.format(
        "Iec60870Device[deviceId=%s, commonAddress=%s, port=%s]",
        this.deviceIdentification, this.commonAddress, this.port);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Iec60870Device)) {
      return false;
    }

    final Iec60870Device device = (Iec60870Device) o;

    return Objects.equals(this.deviceIdentification, device.deviceIdentification);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.deviceIdentification);
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public DeviceType getDeviceType() {
    return this.deviceType;
  }

  public Integer getCommonAddress() {
    return this.commonAddress;
  }

  public void setCommonAddress(final Integer commonAddress) {
    this.commonAddress = commonAddress;
  }

  public Integer getPort() {
    return this.port;
  }

  public void setPort(final Integer port) {
    this.port = port;
  }

  public String getGatewayDeviceIdentification() {
    return this.gatewayDeviceIdentification;
  }

  public void setGatewayDeviceIdentification(final String gatewayDeviceIdentification) {
    this.gatewayDeviceIdentification = gatewayDeviceIdentification;
  }

  public Integer getInformationObjectAddress() {
    return this.informationObjectAddress;
  }

  public void setInformationObjectAddress(final Integer informationObjectAddress) {
    this.informationObjectAddress = informationObjectAddress;
  }

  public boolean hasGatewayDevice() {
    return StringUtils.isNotBlank(this.gatewayDeviceIdentification);
  }

  public String getConnectionDeviceIdentification() {
    if (this.hasGatewayDevice()) {
      return this.gatewayDeviceIdentification;
    }
    return this.deviceIdentification;
  }
}
