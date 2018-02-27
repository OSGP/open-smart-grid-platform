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
import java.util.ArrayList;
import java.util.List;

public class ConfigurationFlagsDto implements Serializable {

    private static final long serialVersionUID = 8360475869038077578L;

    private List<ConfigurationFlagDto> configurationFlag;

    public ConfigurationFlagsDto(final List<ConfigurationFlagDto> configurationFlag) {
        this.configurationFlag = new ArrayList<>(configurationFlag);
    }

    @Override
    public String toString() {
        return this.configurationFlag == null ? "Flags[none]" : String.format("Flags%s", this.configurationFlag);
    }

    public List<ConfigurationFlagDto> getConfigurationFlag() {
        return new ArrayList<>(this.configurationFlag);
    }
}
