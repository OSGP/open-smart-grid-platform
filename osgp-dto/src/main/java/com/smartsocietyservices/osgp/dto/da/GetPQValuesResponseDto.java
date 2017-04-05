package com.smartsocietyservices.osgp.dto.da;

import com.smartsocietyservices.osgp.dto.da.iec61850.DataSampleDto;

import java.io.Serializable;
import java.util.List;

public class GetPQValuesResponseDto implements Serializable {
    private List<DataSampleDto> samples;

    public GetPQValuesResponseDto( final List<DataSampleDto> samples ) {
        this.samples = samples;
    }

    public List<DataSampleDto> getSamples() {
        return samples;
    }
}
