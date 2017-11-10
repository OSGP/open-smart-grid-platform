package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class GetMBusEncryptionKeyStatusRequestData extends MBusActionRequest {

    private static final long serialVersionUID = 3636769765482239443L;

    public GetMBusEncryptionKeyStatusRequestData(final String mBusDeviceIdentification) {
        super(mBusDeviceIdentification);
    }

    @Override
    public void validate() throws FunctionalException {
        // No validation needed

    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.GET_M_BUS_ENCRYPTION_KEY_STATUS;
    }

}
