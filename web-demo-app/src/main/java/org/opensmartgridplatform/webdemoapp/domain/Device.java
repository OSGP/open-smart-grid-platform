/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdemoapp.domain;



/**
 * An entity class which contains the data of a single device.
 */
public class Device  {

    private String deviceIdentification;

    private String containerCity;

    private String containerPostalCode;

    private String containerStreet;

    private String containerNumber;

    private String region;

    private String transitionOrganisationIdentification;

    private boolean activated;

    private boolean hasSchedule;

    private String activatedText;

    private String hasScheduleText;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getContainerCity() {
        return this.containerCity;
    }

    public void setContainerCity(final String containerCity) {
        this.containerCity = containerCity;
    }

    public String getContainerStreet() {
        return this.containerStreet;
    }

    public void setContainerStreet(final String containerStreet) {
        this.containerStreet = containerStreet;
    }

    public String getContainerPostalCode() {
        return this.containerPostalCode;
    }

    public void setContainerPostalCode(final String containerPostalCode) {
        this.containerPostalCode = containerPostalCode;
    }

    public String getContainerNumber() {
        return this.containerNumber;
    }

    public void setContainerNumber(final String containerNumber) {
        this.containerNumber = containerNumber;
    }

    public String getActivatedText() {
        return this.activatedText;
    }

    public void setActivatedText(final String activatedText) {
        this.activatedText = activatedText;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(final boolean activated) {
        this.activated = activated;
    }

    public String getHasScheduleText() {
        return this.hasScheduleText;
    }

    public void setHasScheduleText(final String hasScheduleText) {
        this.hasScheduleText = hasScheduleText;
    }

    public boolean hasSchedule() {
        return this.hasSchedule;
    }

    public void setHasSchedule(final boolean hasSchedule) {
        this.hasSchedule = hasSchedule;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(final String region) {
        this.region = region;
    }

    public String getTransitionOrganisationIdentification() {
        return this.transitionOrganisationIdentification;
    }

    public void setTransitionOrganisationIdentification(final String transitionOrganisationIdentification) {
        this.transitionOrganisationIdentification = transitionOrganisationIdentification;
    }
}