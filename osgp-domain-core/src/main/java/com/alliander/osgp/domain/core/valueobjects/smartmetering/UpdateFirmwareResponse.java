package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.util.List;

public class UpdateFirmwareResponse extends FirmwareVersionResponse {

    private static final long serialVersionUID = 7383932230545675913L;

    public UpdateFirmwareResponse(List<FirmwareVersion> firmwareVersions) {
        super(firmwareVersions);
    }

}
