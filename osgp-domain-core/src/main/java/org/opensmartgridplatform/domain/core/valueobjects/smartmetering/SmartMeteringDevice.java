/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class SmartMeteringDevice implements Serializable {

    private static final long serialVersionUID = -6133164707489276802L;

    private String deviceIdentification;

    private String deviceType;

    private String communicationMethod;

    private String communicationProvider;

    private String iccId;

    private String dsmrVersion;

    private byte[] masterKey;

    private byte[] globalEncryptionUnicastKey;

    private byte[] authenticationKey;

    private String supplier;

    private boolean HLS3Active;

    private boolean HLS4Active;

    private boolean HLS5Active;

    private Date deliveryDate;

    private Long mbusIdentificationNumber;

    private String mbusManufacturerIdentification;

    private Short mbusVersion;

    private Short mbusDeviceTypeIdentification;

    private byte[] mbusDefaultKey;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
    }

    public String getCommunicationMethod() {
        return this.communicationMethod;
    }

    public void setCommunicationMethod(final String communicationMethod) {
        this.communicationMethod = communicationMethod;
    }

    public void setICCId(final String value) {
        this.iccId = value;
    }

    public String getICCId() {
        return this.iccId;
    }

    public String getDSMRVersion() {
        return this.dsmrVersion;
    }

    public void setDSMRVersion(final String dsmrVersion) {
        this.dsmrVersion = dsmrVersion;
    }

    public byte[] getMasterKey() {
        return this.masterKey;
    }

    public void setMasterKey(final byte[] masterKey) {
        this.masterKey = masterKey;
    }

    public byte[] getGlobalEncryptionUnicastKey() {
        return this.globalEncryptionUnicastKey;
    }

    public void setGlobalEncryptionUnicastKey(final byte[] globalEncryptionUnicastKey) {
        this.globalEncryptionUnicastKey = globalEncryptionUnicastKey;
    }

    public byte[] getAuthenticationKey() {
        return this.authenticationKey;
    }

    public void setAuthenticationKey(final byte[] authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public String getCommunicationProvider() {
        return this.communicationProvider;
    }

    public void setCommunicationProvider(final String communicationProvider) {
        this.communicationProvider = communicationProvider;
    }

    public String getSupplier() {
        return this.supplier;
    }

    public void setSupplier(final String supplier) {
        this.supplier = supplier;
    }

    public boolean isHLS3Active() {
        return this.HLS3Active;
    }

    public void setHLS3Active(final boolean hLS3Active) {
        this.HLS3Active = hLS3Active;
    }

    public boolean isHLS4Active() {
        return this.HLS4Active;
    }

    public void setHLS4Active(final boolean hLS4Active) {
        this.HLS4Active = hLS4Active;
    }

    public boolean isHLS5Active() {
        return this.HLS5Active;
    }

    public void setHLS5Active(final boolean hLS5Active) {
        this.HLS5Active = hLS5Active;
    }

    public Date getDeliveryDate() {
        return this.deliveryDate;
    }

    public void setDeliveryDate(final Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getMbusIdentificationNumber() {
        return this.mbusIdentificationNumber;
    }

    public void setMbusIdentificationNumber(final Long mbusIdentificationNumber) {
        this.mbusIdentificationNumber = mbusIdentificationNumber;
    }

    public String getMbusManufacturerIdentification() {
        return this.mbusManufacturerIdentification;
    }

    public void setMbusManufacturerIdentification(final String mbusManufacturerIdentification) {
        this.mbusManufacturerIdentification = mbusManufacturerIdentification;
    }

    public Short getMbusVersion() {
        return this.mbusVersion;
    }

    public void setMbusVersion(final Short mbusVersion) {
        this.mbusVersion = mbusVersion;
    }

    public Short getMbusDeviceTypeIdentification() {
        return this.mbusDeviceTypeIdentification;
    }

    public void setMbusDeviceTypeIdentification(final Short mbusDeviceTypeIdentification) {
        this.mbusDeviceTypeIdentification = mbusDeviceTypeIdentification;
    }

    public byte[] getMbusDefaultKey() {
        return this.mbusDefaultKey;
    }

    public void setMbusDefaultKey(final byte[] mbusDefaultKey) {
        this.mbusDefaultKey = mbusDefaultKey;
    }
}
