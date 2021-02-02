package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualPowerQualityPrivateResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = -7486892829129884160L;

    private final Date logTime;
    
    private final DlmsMeterValueDto instantaneousCurrentL1;
    private final DlmsMeterValueDto instantaneousCurrentL2;
    private final DlmsMeterValueDto instantaneousCurrentL3;
    private final DlmsMeterValueDto instantaneousActivePowerImport;
    private final DlmsMeterValueDto instantaneousActivePowerExport;
    private final DlmsMeterValueDto instantaneousActivePowerImportL1;
    private final DlmsMeterValueDto instantaneousActivePowerImportL2;
    private final DlmsMeterValueDto instantaneousActivePowerImportL3;
    private final DlmsMeterValueDto instantaneousActivePowerExportL1;
    private final DlmsMeterValueDto instantaneousActivePowerExportL2;
    private final DlmsMeterValueDto instantaneousActivePowerExportL3;
    private final DlmsMeterValueDto averageCurrentL1;
    private final DlmsMeterValueDto averageCurrentL2;
    private final DlmsMeterValueDto averageCurrentL3;
    private final DlmsMeterValueDto averageActivePowerImportL1;
    private final DlmsMeterValueDto averageActivePowerImportL2;
    private final DlmsMeterValueDto averageActivePowerImportL3;
    private final DlmsMeterValueDto averageActivePowerExportL1;
    private final DlmsMeterValueDto averageActivePowerExportL2;
    private final DlmsMeterValueDto averageActivePowerExportL3;
    private final DlmsMeterValueDto averageReactivePowerImportL1;
    private final DlmsMeterValueDto averageReactivePowerImportL2;
    private final DlmsMeterValueDto averageReactivePowerImportL3;
    private final DlmsMeterValueDto averageReactivePowerExportL1;
    private final DlmsMeterValueDto averageReactivePowerExportL2;
    private final DlmsMeterValueDto averageReactivePowerExportL3;
    private final DlmsMeterValueDto instantaneousActiveCurrentTotalOverAllPhases;

    public ActualPowerQualityPrivateResponseDto(final Date logTime, 
            final DlmsMeterValueDto instantaneousCurrentL1, 
            final DlmsMeterValueDto instantaneousCurrentL2, 
            final DlmsMeterValueDto instantaneousCurrentL3, 
            final DlmsMeterValueDto instantaneousActivePowerImport, 
            final DlmsMeterValueDto instantaneousActivePowerExport, 
            final DlmsMeterValueDto instantaneousActivePowerImportL1, 
            final DlmsMeterValueDto instantaneousActivePowerImportL2, 
            final DlmsMeterValueDto instantaneousActivePowerImportL3, 
            final DlmsMeterValueDto instantaneousActivePowerExportL1, 
            final DlmsMeterValueDto instantaneousActivePowerExportL2, 
            final DlmsMeterValueDto instantaneousActivePowerExportL3, 
            final DlmsMeterValueDto averageCurrentL1, 
            final DlmsMeterValueDto averageCurrentL2, 
            final DlmsMeterValueDto averageCurrentL3, 
            final DlmsMeterValueDto averageActivePowerImportL1, 
            final DlmsMeterValueDto averageActivePowerImportL2, 
            final DlmsMeterValueDto averageActivePowerImportL3, 
            final DlmsMeterValueDto averageActivePowerExportL1, 
            final DlmsMeterValueDto averageActivePowerExportL2, 
            final DlmsMeterValueDto averageActivePowerExportL3, 
            final DlmsMeterValueDto averageReactivePowerImportL1, 
            final DlmsMeterValueDto averageReactivePowerImportL2, 
            final DlmsMeterValueDto averageReactivePowerImportL3, 
            final DlmsMeterValueDto averageReactivePowerExportL1, 
            final DlmsMeterValueDto averageReactivePowerExportL2, 
            final DlmsMeterValueDto averageReactivePowerExportL3, 
            final DlmsMeterValueDto instantaneousActiveCurrentTotalOverAllPhases) {
        
        this.logTime = new Date(logTime.getTime());
        this.instantaneousCurrentL1 = instantaneousCurrentL1;
        this.instantaneousCurrentL2 = instantaneousCurrentL2;
        this.instantaneousCurrentL3 = instantaneousCurrentL3;
        this.instantaneousActivePowerImport = instantaneousActivePowerImport;
        this.instantaneousActivePowerExport = instantaneousActivePowerExport;
        this.instantaneousActivePowerImportL1 = instantaneousActivePowerImportL1;
        this.instantaneousActivePowerImportL2 = instantaneousActivePowerImportL2;
        this.instantaneousActivePowerImportL3 = instantaneousActivePowerImportL3;
        this.instantaneousActivePowerExportL1 = instantaneousActivePowerExportL1;
        this.instantaneousActivePowerExportL2 = instantaneousActivePowerExportL2;
        this.instantaneousActivePowerExportL3 = instantaneousActivePowerExportL3;
        this.averageCurrentL1 = averageCurrentL1;
        this.averageCurrentL2 = averageCurrentL2;
        this.averageCurrentL3 = averageCurrentL3;
        this.averageActivePowerImportL1 = averageActivePowerImportL1;
        this.averageActivePowerImportL2 = averageActivePowerImportL2;
        this.averageActivePowerImportL3 = averageActivePowerImportL3;
        this.averageActivePowerExportL1 = averageActivePowerExportL1;
        this.averageActivePowerExportL2 = averageActivePowerExportL2;
        this.averageActivePowerExportL3 = averageActivePowerExportL3;
        this.averageReactivePowerImportL1 = averageReactivePowerImportL1;
        this.averageReactivePowerImportL2 = averageReactivePowerImportL2;
        this.averageReactivePowerImportL3 = averageReactivePowerImportL3;
        this.averageReactivePowerExportL1 = averageReactivePowerExportL1;
        this.averageReactivePowerExportL2 = averageReactivePowerExportL2;
        this.averageReactivePowerExportL3 = averageReactivePowerExportL3;
        this.instantaneousActiveCurrentTotalOverAllPhases = instantaneousActiveCurrentTotalOverAllPhases;
    }

}
