package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetPowerQualityProfileResponseDataDto extends ActionResponseDto {

    private static final long serialVersionUID = -156966569210717654L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPowerQualityProfileResponseDataDto.class);

    public List<PowerQualityProfileDataDto> getPowerQualityProfileResponseDataDtos() {
        return this.responses;
    }

    private final List<PowerQualityProfileDataDto> responses;

    public GetPowerQualityProfileResponseDataDto() {
        super();

        this.responses = new ArrayList<>();
    }

    public void addResponseData(final PowerQualityProfileDataDto responseDataDto) {

        LOGGER.info("----- added LogicalName {}", responseDataDto.getLogicalName());
        for (final CaptureObjectDto cDto : responseDataDto.getCaptureObjects()) {
            LOGGER.info("----- added CaptureObjectDto {} - {}", cDto.getLogicalName(), cDto.getUnit());
        }

        LOGGER.info("----- added ProfileEntryValueDto {}", responseDataDto.getProfileEntries().size());

        this.responses.add(responseDataDto);
    }

}
