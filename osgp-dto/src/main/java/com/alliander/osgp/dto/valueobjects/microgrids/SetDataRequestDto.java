/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.List;

public class SetDataRequestDto implements Serializable {

    private static final long serialVersionUID = 2354993345497992666L;

    private List<SetDataSystemIdentifierDto> setDataSystemIdentifiers;

    public SetDataRequestDto(final List<SetDataSystemIdentifierDto> setDataSystemIdentifiers) {
        super();
        this.setDataSystemIdentifiers = setDataSystemIdentifiers;
    }

    public List<SetDataSystemIdentifierDto> getSetDataSystemIdentifiers() {
        return this.setDataSystemIdentifiers;
    }

    public void setSetDataSystemIdentifiers(final List<SetDataSystemIdentifierDto> setDataSystemIdentifiers) {
        this.setDataSystemIdentifiers = setDataSystemIdentifiers;
    }
}
