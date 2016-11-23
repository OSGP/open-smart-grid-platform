package com.alliander.osgp.platform.dlms.cucumber.builders.entities;

import java.util.Map;

public interface CucumberBuilder<T> extends Builder<T> {
    CucumberBuilder<T> withSettings(final Map<String, String> inputSettings);
}
