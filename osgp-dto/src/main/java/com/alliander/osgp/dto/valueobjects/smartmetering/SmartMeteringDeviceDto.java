/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class SmartMeteringDeviceDto implements Serializable {

    private static final long serialVersionUID = -6133164707489276802L;

    private String deviceIdentification;

    private String deviceType;

    private String communicationMethod;

    private String communicationProvider;

    private String iccId;

    private String dsmrVersion;

    private String masterKey;

    private String globalEncryptionUnicastKey;

    private String authenticationKey;

    private String supplier;

    private boolean hls3Active;

    private boolean hls4Active;

    private boolean hls5Active;

    private Date deliveryDate;

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

    public String getMasterKey() {
        return this.masterKey;
    }

    public void setMasterKey(final String masterKey) {
        this.masterKey = masterKey;
    }

    public String getGlobalEncryptionUnicastKey() {
        return this.globalEncryptionUnicastKey;
    }

    public void setGlobalEncryptionUnicastKey(final String globalEncryptionUnicastKey) {
        this.globalEncryptionUnicastKey = globalEncryptionUnicastKey;
    }

    public String getAuthenticationKey() {
        return this.authenticationKey;
    }

    public void setAuthenticationKey(final String authenticationKey) {
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
        return this.hls3Active;
    }

    public void setHLS3Active(final boolean hLS3Active) {
        this.hls3Active = hLS3Active;
    }

    public boolean isHLS4Active() {
        return this.hls4Active;
    }

    public void setHLS4Active(final boolean hLS4Active) {
        this.hls4Active = hLS4Active;
    }

    public boolean isHLS5Active() {
        return this.hls5Active;
    }

    public void setHLS5Active(final boolean hLS5Active) {
        this.hls5Active = hLS5Active;
    }

    public Date getDeliveryDate() {
        return new Date(this.deliveryDate.getTime());
    }

    public void setDeliveryDate(final Date deliveryDate) {
        this.deliveryDate = new Date(deliveryDate.getTime());
    }
}
