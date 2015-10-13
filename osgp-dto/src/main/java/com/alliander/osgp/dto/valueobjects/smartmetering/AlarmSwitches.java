/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class AlarmSwitches implements Serializable {

    private static final long serialVersionUID = -7673193267491776680L;

    private String deviceIdentification;

    private Set<AlarmType> enableAlarms;

    private Set<AlarmType> disableAlarms;

    public AlarmSwitches() {
        this.deviceIdentification = "";
        this.enableAlarms = Collections.emptySet();
        this.disableAlarms = Collections.emptySet();
    }

    public AlarmSwitches(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        this.enableAlarms = Collections.emptySet();
        this.disableAlarms = Collections.emptySet();
    }

    public AlarmSwitches(final String deviceIdentification, final Set<AlarmType> enableAlarms,
            final Set<AlarmType> disableAlarms) {
        this.deviceIdentification = deviceIdentification;
        this.enableAlarms = enableAlarms;
        this.disableAlarms = disableAlarms;
    }

    @Override
    public String toString() {
        return "AlarmSwitches[device=" + this.deviceIdentification + ", enable="
                + (this.enableAlarms == null ? "" : this.enableAlarms.toString()) + ", disable="
                + (this.disableAlarms == null ? "" : this.disableAlarms.toString()) + "]";
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public Set<AlarmType> getEnableAlarms() {
        return this.enableAlarms;
    }

    public void setEnableAlarms(final Set<AlarmType> enableAlarms) {
        this.enableAlarms = enableAlarms;
    }

    public Set<AlarmType> getDisableAlarms() {
        return this.disableAlarms;
    }

    public void setDisableAlarms(final Set<AlarmType> disableAlarms) {
        this.disableAlarms = disableAlarms;
    }
}
