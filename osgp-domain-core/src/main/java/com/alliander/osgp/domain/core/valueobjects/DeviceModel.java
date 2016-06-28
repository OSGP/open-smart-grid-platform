/**
 * Copyright 2014-2016 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects;


public class DeviceModel {

    private String manufacturer;

    private String modelCode;

    private String description;

    public DeviceModel() {
        // Default constructor
    }

    public DeviceModel(final String manufacturer, final String modelCode, final String description) {
        this.manufacturer = manufacturer;
        this.modelCode = modelCode;
        this.description = description;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModelCode() {
        return this.modelCode;
    }

    public void setModelCode(final String modelCode) {
        this.modelCode = modelCode;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

}
