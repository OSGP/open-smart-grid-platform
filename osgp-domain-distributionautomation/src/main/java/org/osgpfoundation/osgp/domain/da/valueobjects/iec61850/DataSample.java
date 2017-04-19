package org.osgpfoundation.osgp.domain.da.valueobjects.iec61850;

import java.io.Serializable;
import java.util.Date;

public class DataSample implements Serializable {
    private static final long serialVersionUID = 4776483459295843436L;

    private String sampleType;
    private Date timestamp;
    private Double value;

    public DataSample( final String sampleType, final Date timestamp, final Double value ) {

        this.sampleType = sampleType;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getSampleType() {
        return sampleType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Double getValue() {
        return value;
    }
}
