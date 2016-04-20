package org.osgp.adapter.protocol.dlms.domain.commands;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;


public interface GetPeriodicMeterReadsBundleCommandExecutor extends CommandExecutor<PeriodicMeterReadsRequestDataDto, ActionResponseDto> {

}
