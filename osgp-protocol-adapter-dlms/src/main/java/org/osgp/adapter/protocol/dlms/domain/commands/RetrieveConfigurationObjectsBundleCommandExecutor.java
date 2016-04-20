package org.osgp.adapter.protocol.dlms.domain.commands;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationRequestDataDto;

public interface RetrieveConfigurationObjectsBundleCommandExecutor extends CommandExecutor<GetConfigurationRequestDataDto, ActionResponseDto> {

}
