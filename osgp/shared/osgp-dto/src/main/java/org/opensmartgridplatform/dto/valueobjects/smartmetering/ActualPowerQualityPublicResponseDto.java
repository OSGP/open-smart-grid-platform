package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualPowerQualityPublicResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = 6475418493823096325L;
    
    private final Date logTime;
    
    private final DlmsMeterValueDto instantaneousVoltageL1;
    private final DlmsMeterValueDto instantaneousVoltageL2;
    private final DlmsMeterValueDto instantaneousVoltageL3;
    private final DlmsMeterValueDto averageVoltageL1;
    private final DlmsMeterValueDto averageVoltageL2;
    private final DlmsMeterValueDto averageVoltageL3;
    private final DlmsMeterValueDto numberOfLongPowerFailures;
    private final DlmsMeterValueDto numberOfPowerFailures;
    private final DlmsMeterValueDto numberOfVoltageSagsForL1;
    private final DlmsMeterValueDto numberOfVoltageSagsForL2;
    private final DlmsMeterValueDto numberOfVoltageSagsForL3;
    private final DlmsMeterValueDto numberOfVoltageSwellsForL1;
    private final DlmsMeterValueDto numberOfVoltageSwellsForL2;
    private final DlmsMeterValueDto numberOfVoltageSwellsForL3;

    public ActualPowerQualityPublicResponseDto(final Date logTime, 
            final DlmsMeterValueDto instantaneousVoltageL1, 
            final DlmsMeterValueDto instantaneousVoltageL2, 
            final DlmsMeterValueDto instantaneousVoltageL3, 
            final DlmsMeterValueDto averageVoltageL1, 
            final DlmsMeterValueDto averageVoltageL2, 
            final DlmsMeterValueDto averageVoltageL3, 
            final DlmsMeterValueDto numberOfLongPowerFailures, 
            final DlmsMeterValueDto numberOfPowerFailures, 
            final DlmsMeterValueDto numberOfVoltageSagsForL1, 
            final DlmsMeterValueDto numberOfVoltageSagsForL2, 
            final DlmsMeterValueDto numberOfVoltageSagsForL3, 
            final DlmsMeterValueDto numberOfVoltageSwellsForL1, 
            final DlmsMeterValueDto numberOfVoltageSwellsForL2, 
            final DlmsMeterValueDto numberOfVoltageSwellsForL3) {
        
        this.logTime = new Date(logTime.getTime());
        this.instantaneousVoltageL1 = instantaneousVoltageL1;
        this.instantaneousVoltageL2 = instantaneousVoltageL2;
        this.instantaneousVoltageL3 = instantaneousVoltageL3;
        this.averageVoltageL1 = averageVoltageL1;
        this.averageVoltageL2 = averageVoltageL2;
        this.averageVoltageL3 = averageVoltageL3;
        this.numberOfLongPowerFailures = numberOfLongPowerFailures;
        this.numberOfPowerFailures = numberOfPowerFailures;
        this.numberOfVoltageSagsForL1 = numberOfVoltageSagsForL1;
        this.numberOfVoltageSagsForL2 = numberOfVoltageSagsForL2;
        this.numberOfVoltageSagsForL3 = numberOfVoltageSagsForL3;
        this.numberOfVoltageSwellsForL1 = numberOfVoltageSwellsForL1;
        this.numberOfVoltageSwellsForL2 = numberOfVoltageSwellsForL2;
        this.numberOfVoltageSwellsForL3 = numberOfVoltageSwellsForL3;

    }

}
