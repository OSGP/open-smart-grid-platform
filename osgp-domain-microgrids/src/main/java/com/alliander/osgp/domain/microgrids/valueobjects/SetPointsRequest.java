/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.microgrids.valueobjects;

import java.io.Serializable;
import java.util.List;

public class SetPointsRequest implements Serializable {

    private static final long serialVersionUID = -6528597730317108512L;

    private final List<SetPointSystemIdentifier> setPointSystemIdentifiers;

    public SetPointsRequest(final List<SetPointSystemIdentifier> setPointSystemIdentifiers) {
        super();
        this.setPointSystemIdentifiers = setPointSystemIdentifiers;
    }

    public List<SetPointSystemIdentifier> getSetPointSystemIdentifiers() {
        return this.setPointSystemIdentifiers;
    }
}
