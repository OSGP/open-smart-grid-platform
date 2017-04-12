package org.osgpfoundation.osgp.domain.da.valueobjects.iec61850;

import java.util.List;

public class LogicalNode {
    private final String name;
    private List<DataSample> data;

    public LogicalNode( final String name, final List<DataSample> data ) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public List<DataSample> getData() {
        return data;
    }
}
