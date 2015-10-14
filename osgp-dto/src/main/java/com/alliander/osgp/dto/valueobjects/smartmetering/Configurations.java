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
import java.util.Set;

public class Configurations implements Serializable {

    private static final long serialVersionUID = 3228867614942565321L;

    private Set<ConfigurationType> configuration;

    public Set<ConfigurationType> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Set<ConfigurationType> configuration) {
        this.configuration = configuration;
    }

}