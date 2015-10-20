/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ConfigurationObject implements Serializable {

    private static final long serialVersionUID = 2955060885937669868L;

    private GprsOperationModeType gprsOperationMode;

    private ConfigurationFlags configurationFlags;

    public GprsOperationModeType getGprsOperationMode() {
        return gprsOperationMode;
    }

    public void setGprsOperationMode(GprsOperationModeType gprsOperationMode) {
        this.gprsOperationMode = gprsOperationMode;
    }

    public ConfigurationFlags getConfigurationFlags() {
        return configurationFlags;
    }

    public void setConfigurationFlags(ConfigurationFlags configurationFlags) {
        this.configurationFlags = configurationFlags;
    }
}