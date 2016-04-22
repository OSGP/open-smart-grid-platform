/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SpecificConfigurationObject implements Serializable {

    private static final long serialVersionUID = 2732162650167261497L;

    private int classId;
    private int attribute;
    private ObisCodeValues obisCode;
    
    
    public SpecificConfigurationObject(int classId, int attribute, ObisCodeValues obisCode) {
        super();
        this.classId = classId;
        this.attribute = attribute;
        this.obisCode = obisCode;
    }

    public int getClassId() {
        return classId;
    }


    public int getAttribute() {
        return attribute;
    }


    public ObisCodeValues getObisCode() {
        return obisCode;
    }

    
}
