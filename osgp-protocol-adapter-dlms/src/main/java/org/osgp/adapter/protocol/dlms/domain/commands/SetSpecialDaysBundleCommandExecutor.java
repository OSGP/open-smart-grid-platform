package org.osgp.adapter.protocol.dlms.domain.commands;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;

public interface SetSpecialDaysBundleCommandExecutor extends CommandExecutor<SpecialDaysRequestDataDto, ActionResponseDto> {

}
