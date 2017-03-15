package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.AccessResultCode;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;

@Component
public class SetClockConfigurationCommandExecutor
        extends AbstractCommandExecutor<SetClockConfigurationRequestDto, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetClockConfigurationCommandExecutor.class);

    public SetClockConfigurationCommandExecutor() {
        super(SetClockConfigurationRequestDto.class);
    }

    @Override
    public AccessResultCode execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final SetClockConfigurationRequestDto object) throws ProtocolAdapterException {

        LOGGER.info("SET CLOCK CONFIGURATION");

        return null;
    }

}
