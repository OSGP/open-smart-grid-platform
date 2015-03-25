package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;
import java.util.List;

public class LightValueMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 6927615839922107231L;

    private List<LightValue> lightValues;

    public LightValueMessageDataContainer(final List<LightValue> lightValues) {
        this.lightValues = lightValues;
    }

    public List<LightValue> getLightValues() {
        return this.lightValues;
    }
}
