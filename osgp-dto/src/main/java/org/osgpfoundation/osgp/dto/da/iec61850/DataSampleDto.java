package org.osgpfoundation.osgp.dto.da.iec61850;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DataSampleDto implements Serializable {
    private static final long serialVersionUID = -5737102492719159340L;

    private String sampleType;
    private Date timestamp;
    private BigDecimal value;

    public DataSampleDto( final String sampleType, final Date timestamp, final BigDecimal value ) {

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

    public BigDecimal getValue() {
        return value;
    }
}
