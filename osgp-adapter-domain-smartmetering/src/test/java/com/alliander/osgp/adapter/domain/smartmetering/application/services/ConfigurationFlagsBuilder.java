/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import java.util.ArrayList;
import java.util.List;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationFlag;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationFlagType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationFlags;

public class ConfigurationFlagsBuilder {

    List<ConfigurationFlag> configurationFlag = new ArrayList<ConfigurationFlag>();

    public ConfigurationFlagsBuilder withConfigurationFlag(final ConfigurationFlagType configurationFlagType) {
        this.configurationFlag.add(new ConfigurationFlag(configurationFlagType, true));
        return this;
    }

    public ConfigurationFlagsBuilder withEmptyList() {
        return this;
    }

    public ConfigurationFlags build() {
        return new ConfigurationFlags(this.configurationFlag);
    }
}
