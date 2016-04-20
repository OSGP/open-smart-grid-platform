package org.osgp.adapter.protocol.dlms.domain.commands;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDataDto;

public interface GetPeriodicMeterReadsGasBundleCommandExecutor extends CommandExecutor<PeriodicMeterReadsGasRequestDataDto, ActionResponseDto> {

}
