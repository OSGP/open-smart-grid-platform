package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class GetMBusEncryptionKeyStatusResponseData extends ActionResponse implements Serializable {

    private static final long serialVersionUID = 3636769765482239443L;

    private String mBusDeviceIdentification;
    private EncryptionKeyStatusType encryptionKeyStatus;

    public GetMBusEncryptionKeyStatusResponseData(final String mBusDeviceIdentification,
            final EncryptionKeyStatusType encryptionKeyStatus) {
        this.encryptionKeyStatus = encryptionKeyStatus;
        this.mBusDeviceIdentification = mBusDeviceIdentification;
    }

    public String getMBusDeviceIdentification() {
        return this.mBusDeviceIdentification;
    }

    public EncryptionKeyStatusType getEncryptionKeyStatus() {
        return this.encryptionKeyStatus;
    }

}
