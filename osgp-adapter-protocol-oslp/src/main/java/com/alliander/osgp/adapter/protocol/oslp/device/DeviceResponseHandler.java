package com.alliander.osgp.adapter.protocol.oslp.device;

public interface DeviceResponseHandler {

    void handleResponse(DeviceResponse deviceResponse);

    void handleException(Throwable t, DeviceResponse deviceResponse);

}
