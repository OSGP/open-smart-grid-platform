package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.List;

import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;

public class UpdateFirmwareResponseDto extends FirmwareVersionResponseDto {

    private static final long serialVersionUID = 8099066390924573498L;

    public UpdateFirmwareResponseDto(final List<FirmwareVersionDto> firmwareVersions) {
        super(firmwareVersions);
    }
}
