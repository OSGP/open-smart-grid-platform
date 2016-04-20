/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

/**
 * Contains a list of all sub data attributes of the IEC61850 Device
 */
public enum SubDataAttribute {

    CURRENT_VERSION("curVer"),
    URL("url"),
    START_TIME("startT");

    private String description;

    private SubDataAttribute(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}
