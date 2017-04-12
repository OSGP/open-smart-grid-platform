package org.osgpfoundation.osgp.dto.da.iec61850;

import java.util.List;

public class LogicalNodeDto {
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
        return data;
    }
}
