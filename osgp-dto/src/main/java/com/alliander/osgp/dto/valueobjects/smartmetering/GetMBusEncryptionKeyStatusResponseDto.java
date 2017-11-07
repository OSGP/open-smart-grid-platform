/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public class GetMBusEncryptionKeyStatusResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = -8661462528133418593L;

    private String mbusDeviceIdentification;
    private EncryptionKeyStatusTypeDto encryptionKeyStatusTypeDto;

    public GetMBusEncryptionKeyStatusResponseDto(final String mbusDeviceIdentification,
            final EncryptionKeyStatusTypeDto encryptionKeyStatusTypeDto) {
        this.mbusDeviceIdentification = mbusDeviceIdentification;
        this.encryptionKeyStatusTypeDto = encryptionKeyStatusTypeDto;
    }

    public String getMbusDeviceIdentification() {
        return this.mbusDeviceIdentification;
    }

    public EncryptionKeyStatusTypeDto getEncryptionKeyStatusTypeDto() {
        return this.encryptionKeyStatusTypeDto;
    }

}