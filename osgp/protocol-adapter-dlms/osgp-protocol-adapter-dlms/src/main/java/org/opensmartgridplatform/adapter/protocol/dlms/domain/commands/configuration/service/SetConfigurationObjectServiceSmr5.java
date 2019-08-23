package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.springframework.stereotype.Component;

@Component
public class SetConfigurationObjectServiceSmr5 extends SetConfigurationObjectService {

    SetConfigurationObjectServiceSmr5(final DlmsHelper dlmsHelper) {
        super(dlmsHelper);
    }

    @Override
    public boolean handles(final Protocol protocol) {
        return protocol.isSmr5();
    }

    @Override
    DataObject buildSetParameterData(final ConfigurationObjectDto configurationToSet,
            final ConfigurationObjectDto configurationOnDevice) {
        return DataObject.newBitStringData(this.getFlags(configurationToSet, configurationOnDevice));
    }

    @Override
    Integer getBitPosition(final ConfigurationFlagTypeDto type) {
        return type.getBitPositionSmr5();
    }
}
