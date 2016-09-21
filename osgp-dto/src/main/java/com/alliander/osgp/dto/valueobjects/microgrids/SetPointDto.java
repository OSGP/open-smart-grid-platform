package com.alliander.osgp.dto.valueobjects.microgrids;

import java.io.Serializable;

import org.joda.time.DateTime;

public class SetPointDto implements Serializable {

    private static final long serialVersionUID = -8242555524743018337L;

    private String node;
    private double value;
    private DateTime startTime;
    private DateTime endTime;

    public SetPointDto(final String node, final double value, final DateTime startTime, final DateTime endTime) {
        super();
        this.node = node;
        this.value = value;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(final String node) {
        this.node = node;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(final double value) {
        this.value = value;
    }

    public DateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(final DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(final DateTime endTime) {
        this.endTime = endTime;
    }
}
