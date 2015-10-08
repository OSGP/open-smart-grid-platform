/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public class SpecialDaysRequestData {

    private String specialDayDate;

    private int dayId;

    public String getSpecialDayDate() {
        return specialDayDate;
    }

    public void setSpecialDayDate(String specialDayDate) {
        this.specialDayDate = specialDayDate;
    }

    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

}