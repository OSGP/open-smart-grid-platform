package com.alliander.osgp.dto.valueobjects.microgrids;

public class SetPointSystemIdentifierDto extends SystemIdentifierDto {

    private static final long serialVersionUID = 1491574329325798488L;

    private SetPointDto setPoint;

    public SetPointSystemIdentifierDto(final int id, final String systemType, final SetPointDto setPoint) {
        super(id, systemType);
        this.setPoint = setPoint;
    }

    public SetPointDto getSetPoint() {
        return this.setPoint;
    }

    public void setSetPoint(final SetPointDto setPoint) {
        this.setPoint = setPoint;
    }

}
