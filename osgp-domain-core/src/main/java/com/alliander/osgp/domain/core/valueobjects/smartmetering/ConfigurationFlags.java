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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigurationFlags implements Serializable {

    private static final long serialVersionUID = 8360475869038077578L;

    private List<ConfigurationFlag> configurationFlag;

    public ConfigurationFlags(final List<ConfigurationFlag> configurationFlag) {
        this.configurationFlag = new ArrayList<>(configurationFlag);
    }

    public List<ConfigurationFlag> getConfigurationFlag() {
        return Collections.unmodifiableList(this.configurationFlag);
    }

    public void setConfigurationFlag(final List<ConfigurationFlag> configurationFlag) {
        this.configurationFlag = configurationFlag;
    }
}
