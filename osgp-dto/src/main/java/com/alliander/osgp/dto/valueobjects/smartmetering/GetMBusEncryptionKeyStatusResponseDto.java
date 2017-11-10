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

    private String mBusDeviceIdentification;
    private EncryptionKeyStatusTypeDto encryptionKeyStatus;

    public GetMBusEncryptionKeyStatusResponseDto(final String mBusDeviceIdentification,
            final EncryptionKeyStatusTypeDto encryptionKeyStatus) {
        this.mBusDeviceIdentification = mBusDeviceIdentification;
        this.encryptionKeyStatus = encryptionKeyStatus;
    }

    public String getMBusDeviceIdentification() {
        return this.mBusDeviceIdentification;
    }

    public EncryptionKeyStatusTypeDto getEncryptionKeyStatus() {
        return this.encryptionKeyStatus;
    }

}