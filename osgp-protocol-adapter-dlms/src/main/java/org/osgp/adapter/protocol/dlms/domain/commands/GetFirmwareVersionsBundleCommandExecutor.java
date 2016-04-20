package org.osgp.adapter.protocol.dlms.domain.commands;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDataDto;

public interface GetFirmwareVersionsBundleCommandExecutor extends CommandExecutor<GetFirmwareVersionRequestDataDto, ActionResponseDto> {

}
