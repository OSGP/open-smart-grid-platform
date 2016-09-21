package com.alliander.osgp.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.List;

public class SetPointsRequestDto implements Serializable {

    private static final long serialVersionUID = 2354993345497992666L;

    private List<SetPointSystemIdentifierDto> setPointSystemIdentifiers;

    public SetPointsRequestDto(final List<SetPointSystemIdentifierDto> setPointSystemIdentifiers) {
        super();
        this.setPointSystemIdentifiers = setPointSystemIdentifiers;
    }

    public List<SetPointSystemIdentifierDto> getSetPointSystemIdentifiers() {
        return this.setPointSystemIdentifiers;
    }

    public void setSetPointSystemIdentifiers(final List<SetPointSystemIdentifierDto> setPointSystemIdentifiers) {
        this.setPointSystemIdentifiers = setPointSystemIdentifiers;
    }
}
