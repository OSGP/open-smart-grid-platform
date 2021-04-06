/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class GetModemInfoResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = 1809460449154417629L;

    private final ModemInfoDto modemInfoDto;

    public GetModemInfoResponseDto(final ModemInfoDto modemInfoDto) {
        this.modemInfoDto = modemInfoDto;
    }

    public ModemInfoDto getModemInfoDto() {
        return this.modemInfoDto;
    }

}
