/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;

public class DeviceChannelsHelperTest {

    private final DlmsHelper dlmsHelper = new DlmsHelper();
    private final DeviceChannelsHelper deviceChannelsHelper = new DeviceChannelsHelper(this.dlmsHelper);

    private static final short PRIMARY_ADDRESS = 1;
    private static final long IDENTIFICATION_NUMBER = 16137489L;
    private static final int MANUFACTURER_IDENTIFICATION = 1057;
    private static final String MANUFACTURER_IDENTIFICATION_AS_TEXT = "AAA";
    private static final short VERSION = 2;
    private static final short DEVICE_TYPE = 3;

    private final GetResult primaryAddress = new GetResultImpl(DataObject.newUInteger8Data(PRIMARY_ADDRESS));
    private final GetResult identificationNumber = new GetResultImpl(
            DataObject.newUInteger32Data(IDENTIFICATION_NUMBER));
    private final GetResult manufacturerIdentification = new GetResultImpl(
            DataObject.newUInteger16Data(MANUFACTURER_IDENTIFICATION));
    private final GetResult version = new GetResultImpl(DataObject.newUInteger8Data(VERSION));
    private final GetResult deviceType = new GetResultImpl(DataObject.newUInteger8Data(DEVICE_TYPE));

    @Test
    public void testMakeChannelElementValues() throws Exception {

        final List<GetResult> resultList = new ArrayList<>(Arrays.asList(this.primaryAddress, this.identificationNumber,
                this.manufacturerIdentification, this.version, this.deviceType));

        final ChannelElementValuesDto values = this.deviceChannelsHelper.makeChannelElementValues((short) 1,
                resultList);

        assertThat(values.getPrimaryAddress()).isEqualTo(PRIMARY_ADDRESS);
        assertThat(values.getIdentificationNumber()).isEqualTo(String.valueOf(IDENTIFICATION_NUMBER));
        assertThat(values.getManufacturerIdentification()).isEqualTo(MANUFACTURER_IDENTIFICATION_AS_TEXT);
        assertThat(values.getVersion()).isEqualTo(VERSION);
        assertThat(values.getDeviceTypeIdentification()).isEqualTo(DEVICE_TYPE);
    }

    @Test
    public void testMakeChannelElementValuesInvalidManufacturerId() throws Exception {

        final GetResult manufacturerIdentificationInvalid = new GetResultImpl(DataObject.newUInteger16Data(123));

        final List<GetResult> resultList = new ArrayList<>(Arrays.asList(this.primaryAddress, this.identificationNumber,
                manufacturerIdentificationInvalid, this.version, this.deviceType));

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            this.deviceChannelsHelper.makeChannelElementValues((short) 1, resultList);
        });
    }

    @Test
    public void testMakeChannelElementValuesIdenfiticationNumberNull() throws Exception {

        final GetResult identificationNumberNull = new GetResultImpl(null);

        final List<GetResult> resultList = new ArrayList<>(Arrays.asList(this.primaryAddress, identificationNumberNull,
                this.manufacturerIdentification, this.version, this.deviceType));

        final ChannelElementValuesDto values = this.deviceChannelsHelper.makeChannelElementValues((short) 1,
                resultList);

        assertThat(values.getIdentificationNumber()).isEqualTo(null);
    }
}
