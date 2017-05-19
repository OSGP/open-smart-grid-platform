package com.alliander.osgp.adapter.domain.smartmetering.application.exception;

import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelElementValues;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;

public class MBusChannelNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public MBusChannelNotFoundException(final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) {
        super(buildErrorMessage(mbusChannelElementsResponseDto));
    }

    private static String buildErrorMessage(final MbusChannelElementsResponseDto mbusChannelElementsResponseDto) {
        final StringBuilder sb = new StringBuilder();
        mbusChannelElementsResponseDto.getChannelElements().forEach(f -> appendMessage(sb, f));
        return sb.toString();
    }

    private static final String MSG_TEMPLATE = "channel: %d, deviceId: %d, manufacturerId: %d, version: %d, type: %d, \n";

    private static void appendMessage(final StringBuilder sb, final ChannelElementValues channelElements) {
        final String msg = String.format(MSG_TEMPLATE, channelElements.getChannel(),
                channelElements.getDeviceTypeIdentification(), channelElements.getManufacturerIdentification(),
                channelElements.getVersion(), channelElements.getDeviceTypeIdentification());
        sb.append(msg);
    }
}
