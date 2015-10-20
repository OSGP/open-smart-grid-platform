/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ConfigurationFlag implements Serializable {

    private static final long serialVersionUID = -7943594696973940504L;

    private ConfigurationFlagType configurationFlagType;

    private boolean enabled;

    public ConfigurationFlagType getConfigurationFlagType() {
        return configurationFlagType;
    }

    public void setConfigurationFlagType(ConfigurationFlagType configurationFlagType) {
        this.configurationFlagType = configurationFlagType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}