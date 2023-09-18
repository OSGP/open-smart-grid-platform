// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class DlmsDevice extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = 3899692163578950343L;

  private static final int KEMA_CODE_LENGTH = 5;

  @Column(unique = true, nullable = false, length = 40)
  private String deviceIdentification;

  @Column private String communicationMethod;

  @Column private String communicationProvider;

  @Column private String iccId;

  @Column private boolean lls1Active;

  @Column private boolean hls3Active;

  @Column private boolean hls4Active;

  @Column private boolean hls5Active;

  @Column private Integer challengeLength;

  @Column private boolean withListSupported;

  @Column private boolean selectiveAccessSupported;

  @Column private boolean selectiveAccessPeriodicMeterReadsSupported = true;

  @Column private boolean ipAddressIsStatic;

  // The following three are optional columns that are used in the device
  // simulator (DeviceServer)
  @Column private Long port;

  @Column private Long clientId;

  @Column private Long logicalId;

  @Column private boolean inDebugMode;

  @Column private boolean useHdlc;

  @Column private boolean useSn;

  @Column private boolean polyphase;

  @Column private Instant keyProcessingStartTime;

  @Column private String mbusIdentificationNumber;

  @Column(length = 3)
  private String mbusManufacturerIdentification;

  @Column(length = 40)
  private String timezone;

  @Column(name = "protocol", nullable = false)
  private String protocolName;

  @Column(nullable = false)
  private String protocolVersion;

  // -- This comes from: Core Device.

  @Transient private String ipAddress;

  // Starting value of the invocation counter for next connection, for SMR device with Hls5
  // connection only (for
  // other devices the invocation counter value is never updated).
  // This value is initialized by reading the invocation counter from the device using the public
  // client.
  // After each session with the device it is incremented with the number of invocations in the
  // session.
  @Column private Long invocationCounter = 0L;

  @Column(length = 64)
  private String firmwareHash;

  public DlmsDevice() {
    // Default constructor
  }

  public DlmsDevice(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public long getDeviceId() {
    final String twelveDigits = this.deviceIdentification.substring(KEMA_CODE_LENGTH);
    return Long.parseLong(twelveDigits);
  }

  @Override
  public String toString() {
    return String.format(
        "DlmsDevice[deviceId=%s, lls1=%b, hls3=%b, hls4=%b, hls5=%b, ipAddress=%s, port=%s, "
            + "logicalId=%s, clientId=%s, "
            + "debug=%b, hdlc=%b, sn=%b, pphase=%b, mbusIdentification=%s, mbusManufacturer=%s, "
            + "protocolName=%s, "
            + "protocolVersion=%s]",
        this.deviceIdentification,
        this.lls1Active,
        this.hls3Active,
        this.hls4Active,
        this.hls5Active,
        this.ipAddress,
        this.port,
        this.logicalId,
        this.clientId,
        this.inDebugMode,
        this.useHdlc,
        this.useSn,
        this.polyphase,
        this.mbusIdentificationNumber,
        this.mbusManufacturerIdentification,
        this.protocolName,
        this.protocolVersion);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DlmsDevice)) {
      return false;
    }

    final DlmsDevice device = (DlmsDevice) o;

    return Objects.equals(this.deviceIdentification, device.deviceIdentification);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.deviceIdentification);
  }

  public boolean isIpAddressIsStatic() {
    return this.ipAddressIsStatic;
  }

  public void setIpAddressIsStatic(final boolean ipAddressIsStatic) {
    this.ipAddressIsStatic = ipAddressIsStatic;
  }

  public String getCommunicationMethod() {
    return this.communicationMethod;
  }

  public void setCommunicationMethod(final String communicationMethod) {
    this.communicationMethod = communicationMethod;
  }

  public String getCommunicationProvider() {
    return this.communicationProvider;
  }

  public void setCommunicationProvider(final String communicationProvider) {
    this.communicationProvider = communicationProvider;
  }

  public void setIccId(final String value) {
    this.iccId = value;
  }

  public String getIccId() {
    return this.iccId;
  }

  public boolean isLls1Active() {
    return this.lls1Active;
  }

  public void setLls1Active(final boolean lls1Active) {
    this.lls1Active = lls1Active;
  }

  public boolean isHls3Active() {
    return this.hls3Active;
  }

  public void setHls3Active(final boolean hls3Active) {
    this.hls3Active = hls3Active;
  }

  public boolean isHls4Active() {
    return this.hls4Active;
  }

  public void setHls4Active(final boolean hls4Active) {
    this.hls4Active = hls4Active;
  }

  public boolean isHls5Active() {
    return this.hls5Active;
  }

  public void setHls5Active(final boolean hls5Active) {
    this.hls5Active = hls5Active;
  }

  public Integer getChallengeLength() {
    return this.challengeLength;
  }

  public void setChallengeLength(final Integer challengeLength) {
    this.challengeLength = challengeLength;
  }

  public boolean isWithListSupported() {
    return this.withListSupported;
  }

  public void setWithListSupported(final boolean withListSupported) {
    this.withListSupported = withListSupported;
  }

  public boolean isSelectiveAccessSupported() {
    return this.selectiveAccessSupported;
  }

  public void setSelectiveAccessSupported(final boolean selectiveAccessSupported) {
    this.selectiveAccessSupported = selectiveAccessSupported;
  }

  public boolean isSelectiveAccessPeriodicMeterReadsSupported() {
    return this.selectiveAccessPeriodicMeterReadsSupported;
  }

  public void setSelectiveAccessPeriodicMeterReadsSupported(
      final boolean selectiveAccessPeriodicMeterReadsSupported) {
    this.selectiveAccessPeriodicMeterReadsSupported = selectiveAccessPeriodicMeterReadsSupported;
  }

  public void setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public Long getPort() {
    return this.port;
  }

  public void setPort(final Long port) {
    this.port = port;
  }

  public Long getClientId() {
    return this.clientId;
  }

  public void setClientId(final Long clientId) {
    this.clientId = clientId;
  }

  public Long getLogicalId() {
    return this.logicalId;
  }

  public void setLogicalId(final Long logicalId) {
    this.logicalId = logicalId;
  }

  public boolean isInDebugMode() {
    return this.inDebugMode;
  }

  public void setInDebugMode(final boolean inDebugMode) {
    this.inDebugMode = inDebugMode;
  }

  public boolean isUseHdlc() {
    return this.useHdlc;
  }

  public void setUseHdlc(final boolean useHdlc) {
    this.useHdlc = useHdlc;
  }

  public boolean isUseSn() {
    return this.useSn;
  }

  public void setUseSn(final boolean useSn) {
    this.useSn = useSn;
  }

  public boolean isPolyphase() {
    return this.polyphase;
  }

  public void setPolyphase(final boolean polyphase) {
    this.polyphase = polyphase;
  }

  public Instant getKeyProcessingStartTime() {
    return this.keyProcessingStartTime;
  }

  public void setKeyProcessingStartTime(final Instant keyProcessingStartTime) {
    this.keyProcessingStartTime = keyProcessingStartTime;
  }

  public String getMbusIdentificationNumber() {
    return this.mbusIdentificationNumber;
  }

  @Transient
  public String getMbusIdentificationNumberTextualRepresentation() {
    if (this.mbusIdentificationNumber == null) {
      return null;
    }
    return this.mbusIdentificationNumber;
  }

  public void setMbusIdentificationNumber(final String mbusIdentificationNumber) {
    this.mbusIdentificationNumber = mbusIdentificationNumber;
  }

  public String getManufacturerId() {
    /*
     * MbusManufacturerIdentification holds ManufacturerId for non M-Bus
     * devices.
     */
    return this.mbusManufacturerIdentification;
  }

  public String getMbusManufacturerIdentification() {
    return this.mbusManufacturerIdentification;
  }

  public void setMbusManufacturerIdentification(final String mbusManufacturerIdentification) {
    this.mbusManufacturerIdentification = mbusManufacturerIdentification;
  }

  public String getProtocolName() {
    return this.protocolName;
  }

  public String getProtocolVersion() {
    return this.protocolVersion;
  }

  public void setProtocol(final String protocol, final String protocolVersion) {
    this.protocolName = protocol;
    this.protocolVersion = protocolVersion;
  }

  public void setProtocol(final Protocol protocol) {
    this.setProtocol(protocol.getName(), protocol.getVersion());
  }

  /**
   * The IP address is not part of the data in the protocol adapter database. The value needs to
   * have been set based on information from the core database before it can be used.
   *
   * @return the device's network address, if it has been explicitly set; otherwise {@code null}.
   */
  public String getIpAddress() {
    return this.ipAddress;
  }

  public void setIpAddress(final String ipAddress) {
    this.ipAddress = ipAddress;
  }

  /** The starting value of the invocation counter for a new Hls5 connection. */
  public Long getInvocationCounter() {
    return this.invocationCounter;
  }

  public boolean isInvocationCounterInitialized() {
    return this.invocationCounter != null;
  }

  public void setInvocationCounter(final Long invocationCounter) {
    this.invocationCounter = invocationCounter;
  }

  public void incrementInvocationCounter(final int amount) {
    this.invocationCounter += amount;
  }

  public boolean needsInvocationCounter() {
    return this.hls5Active && "SMR".equals(this.protocolName);
  }

  public boolean communicateUnencrypted() {
    return !(this.hls3Active || this.hls4Active || this.hls5Active);
  }

  public String getTimezone() {
    return this.timezone;
  }

  public void setTimezone(final String timezone) {
    this.timezone = timezone;
  }

  public String getFirmwareHash() {
    return this.firmwareHash;
  }

  public void setFirmwareHash(final String firmwareHash) {
    this.firmwareHash = firmwareHash;
  }
}
