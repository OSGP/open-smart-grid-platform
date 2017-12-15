/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public class ClearAlarmRegisterRequestDto implements ActionRequestDto {

    private static final long serialVersionUID = 8564943872758612188L;

    private long alarmCode = 0;

    public ClearAlarmRegisterRequestDto(final int alarmCode) {
        this.alarmCode = alarmCode;
    }

    public long getAlarmCode() {
        return this.alarmCode;
    }

}
