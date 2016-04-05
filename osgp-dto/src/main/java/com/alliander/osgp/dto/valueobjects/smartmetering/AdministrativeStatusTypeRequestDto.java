/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public class AdministrativeStatusTypeRequestDto extends ActionValueObjectResponseDto {

    private static final long serialVersionUID = -5725945882158617131L;

    private AdministrativeStatusTypeDto administrativeStatusTypeDto;

    public AdministrativeStatusTypeRequestDto(final AdministrativeStatusTypeDto administrativeStatusTypeDto) {
        this.administrativeStatusTypeDto = administrativeStatusTypeDto;
    }

    public AdministrativeStatusTypeDto getAdministrativeStatusTypeDto() {
        return this.administrativeStatusTypeDto;
    }

    public void setAdministrativeStatusTypeDto(final AdministrativeStatusTypeDto administrativeStatusTypeDto) {
        this.administrativeStatusTypeDto = administrativeStatusTypeDto;
    }

}