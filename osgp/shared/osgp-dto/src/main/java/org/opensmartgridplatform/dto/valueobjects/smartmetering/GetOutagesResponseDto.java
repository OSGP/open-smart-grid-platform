/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.List;

public class GetOutagesResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = 3953818299926960294L;

    private List<OutageDto> outages;

    public GetOutagesResponseDto(final List<OutageDto> outages) {
        this.outages = outages;
    }

    public List<OutageDto> getOutages() {
        return this.outages;
    }
}
