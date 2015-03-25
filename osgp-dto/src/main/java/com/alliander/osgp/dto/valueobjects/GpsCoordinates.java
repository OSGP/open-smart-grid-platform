package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class GpsCoordinates implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -8621653416475365264L;

    private Float latitude;
    private Float longitude;

    public GpsCoordinates(final Float latitude, final Float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return this.latitude;
    }

    public Float getLongitude() {
        return this.longitude;
    }
}
