package org.osgpfoundation.osgp.dto.da.iec61850;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class LogicalNodeDto implements Serializable {
    private static final long serialVersionUID = -5205998771499617879L;

    private final String name;
    private List<DataSampleDto> data;

    public LogicalNodeDto( final String name, final List<DataSampleDto> data ) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public List<DataSampleDto> getData() {
        return Collections.unmodifiableList(data);
    }
}
