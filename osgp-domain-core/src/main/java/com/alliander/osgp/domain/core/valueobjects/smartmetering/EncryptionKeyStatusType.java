/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

public enum EncryptionKeyStatusType {

    NO_ENCRYPTION_KEY(0),
    ENCRYPTION_KEY_SET(1),
    ENCRYPTION_KEY_TRANSFERED(2),
    ENCRYPTION_KEY_SET_AND_TRANSFERRED(3),
    ENCRYPTION_KEY_IN_USE(4);

    private int value;

    private EncryptionKeyStatusType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
