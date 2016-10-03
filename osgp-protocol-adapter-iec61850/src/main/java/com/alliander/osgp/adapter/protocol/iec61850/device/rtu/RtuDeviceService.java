/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.rtu;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.requests.GetDataDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.requests.SetSetPointsDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.GetDataDeviceResponse;
import com.alliander.osgp.dto.valueobjects.microgrids.DataRequestDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SetPointsRequestDto;

public interface RtuDeviceService {
    /**
     * Reads the {@link DataRequestDto} from the device.
     *
     * @returns a {@link GetDataDeviceResponse} via the deviceResponseHandler's
     *          callback.
     */
    void getData(GetDataDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler);

    /**
     * Writes the {@link SetPointsRequestDto} to the device.
     *
     * @returns a {@link DeviceMessageStatus} via the deviceResponseHandler's
     *          callback.
     */
    void setSetPoints(SetSetPointsDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler);
}
