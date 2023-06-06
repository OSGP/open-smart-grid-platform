// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Table(name = "iec61850_device")
public class Iec61850Device extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = 182081847594067712L;

  @Column(unique = true, nullable = false, length = 40)
  private String deviceIdentification;

  @Column(length = 40)
  private String icdFilename;

  @Column private Integer port;

  @Column private String serverName;

  @Column private boolean enableAllReportsOnConnect;

  @Column private boolean useCombinedLoad;

  public Iec61850Device() {
    // Default constructor
  }

  public Iec61850Device(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  @Override
  public String toString() {
    return String.format(
        "Iec61850Device[deviceId=%s, icdFilename=%s, port=%s]",
        this.deviceIdentification, this.icdFilename, this.port);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Iec61850Device)) {
      return false;
    }

    final Iec61850Device device = (Iec61850Device) o;

    return Objects.equals(this.deviceIdentification, device.deviceIdentification);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.deviceIdentification);
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getIcdFilename() {
    return this.icdFilename;
  }

  public void setIcdFilename(final String icdFilename) {
    this.icdFilename = icdFilename;
  }

  public Integer getPort() {
    return this.port;
  }

  public void setPort(final Integer port) {
    this.port = port;
  }

  public String getServerName() {
    return this.serverName;
  }

  public void setServerName(final String serverName) {
    this.serverName = serverName;
  }

  public boolean isEnableAllReportsOnConnect() {
    return this.enableAllReportsOnConnect;
  }

  public void setEnableAllReportsOnConnect(final boolean enableAllReportsOnConnect) {
    this.enableAllReportsOnConnect = enableAllReportsOnConnect;
  }

  public boolean isUseCombinedLoad() {
    return this.useCombinedLoad;
  }

  public void setUseCombinedLoad(final boolean useCombinedLoad) {
    this.useCombinedLoad = useCombinedLoad;
  }
}
