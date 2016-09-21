/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.microgrids.valueobjects;

public class SetPointSystemIdentifier extends SystemIdentifier {

    private static final long serialVersionUID = 9130054367163068097L;

    private final SetPoint setPoint;

    public SetPointSystemIdentifier(final int id, final String systemType, final SetPoint setPoint) {
        super(id, systemType);
        this.setPoint = setPoint;
    }

    public SetPoint getSetPoint() {
        return this.setPoint;
    }
}
