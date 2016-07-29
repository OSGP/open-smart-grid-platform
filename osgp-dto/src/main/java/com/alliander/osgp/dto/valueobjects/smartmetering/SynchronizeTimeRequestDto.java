/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;


public class SynchronizeTimeRequestDto implements ActionRequestDto {

    private static final long serialVersionUID = -4724182593235620894L;

    private final int offset;

    private final boolean dst;

    public SynchronizeTimeRequestDto(final int offset, final boolean dst) {
        this.offset = offset;
        this.dst = dst;
    }

    public int getOffset() {
        return this.offset;
    }

    public boolean isDst() {
        return this.dst;
    }
}
