/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BundleMessageDataContainerDto implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4268617729741836833L;

    private List<ActionValueObjectDto> actionList;

    public BundleMessageDataContainerDto(final List<ActionValueObjectDto> actionList) {
        this.actionList = actionList;
    }

    public List<ActionValueObjectDto> getActionList() {
        return new ArrayList<ActionValueObjectDto>(this.actionList);
    }

}
