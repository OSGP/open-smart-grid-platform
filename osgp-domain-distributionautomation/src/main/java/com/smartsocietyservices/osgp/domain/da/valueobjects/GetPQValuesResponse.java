package com.smartsocietyservices.osgp.domain.da.valueobjects;

import com.smartsocietyservices.osgp.domain.da.valueobjects.iec61850.DataSample;

import java.io.Serializable;
import java.util.List;

public class GetPQValuesResponse implements Serializable{
    private List<DataSample> samples;

    public GetPQValuesResponse( final List<DataSample> samples ) {
        this.samples = samples;
    }

    public List<DataSample> getSamples() {
        return samples;
    }
}
