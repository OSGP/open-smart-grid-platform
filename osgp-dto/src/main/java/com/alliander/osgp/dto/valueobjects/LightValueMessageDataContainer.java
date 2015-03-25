package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

public class LightValueMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 4725254533964342905L;

    private List<LightValue> lightValues;

    public LightValueMessageDataContainer(final List<LightValue> lightValues) {
        this.lightValues = lightValues;
    }

    public List<LightValue> getLightValues() {
        return this.lightValues;
    }
}
