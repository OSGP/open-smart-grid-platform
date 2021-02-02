package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class ActualPowerQualityRequestDto implements ActionRequestDto {

    private static final long serialVersionUID = 1544769605230783612L;

    private final ConfidentialityTypeDto confidentialityTypeDto;

    public ActualPowerQualityRequestDto() {
        this(null);
    }

    public ActualPowerQualityRequestDto(final ConfidentialityTypeDto confidentialityTypeDto) {
        this.confidentialityTypeDto = confidentialityTypeDto;
    }

    public boolean isPublic() {
        return this.confidentialityTypeDto != null && this.confidentialityTypeDto == ConfidentialityTypeDto.PUBLIC;
    }

    public ConfidentialityTypeDto getConfidentiality() {
        return this.confidentialityTypeDto;
    }

}
