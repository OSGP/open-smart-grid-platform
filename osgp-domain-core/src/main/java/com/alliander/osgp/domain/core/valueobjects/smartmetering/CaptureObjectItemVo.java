/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class CaptureObjectItemVo implements Serializable {

    private static final long serialVersionUID = 991045734132231909L;

    private CaptureObjectVo captureObject;

    public CaptureObjectItemVo(CaptureObjectVo captureObject) {
        super();
        this.captureObject = captureObject;
    }

    public CaptureObjectVo getCaptureObject() {
        return this.captureObject;
    }

}
