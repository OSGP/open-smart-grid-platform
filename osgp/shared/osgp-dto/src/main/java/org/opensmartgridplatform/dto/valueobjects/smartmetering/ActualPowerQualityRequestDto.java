/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class ActualPowerQualityRequestDto implements ActionRequestDto {

    private static final long serialVersionUID = 1544769605230783612L;

    private final String profileType;

    public ActualPowerQualityRequestDto(final String profileType) {
        this.profileType = profileType;
    }

    public String getProfileType() {
        return this.profileType;
    }

}
