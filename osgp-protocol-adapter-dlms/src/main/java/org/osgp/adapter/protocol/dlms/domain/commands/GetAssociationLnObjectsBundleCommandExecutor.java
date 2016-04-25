package org.osgp.adapter.protocol.dlms.domain.commands;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAssociationLnObjectsRequestDataDto;

public interface GetAssociationLnObjectsBundleCommandExecutor extends
CommandExecutor<GetAssociationLnObjectsRequestDataDto, ActionResponseDto> {

}
