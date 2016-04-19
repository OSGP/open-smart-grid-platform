package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ObjectListElementDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObjectListTypeDto;

@Component
public class GetAssociationLnObjectsCommandExecutor implements CommandExecutor<Void, ObjectListTypeDto> {

    private static final ObisCode OBIS_CODE = new ObisCode("0.0.40.0.0.255");
    private static final int ATTRIBUTE_ID = 2;

    @Override
    public ObjectListTypeDto execute(final ClientConnection conn, final DlmsDevice device, final Void object)
            throws ProtocolAdapterException {

        // TODO replace dummy implementation.
        final List<ObjectListElementDto> elements = new ArrayList<>();
        elements.add(new ObjectListElementDto(1L, 1, "0.0.2555.255.255.255"));
        elements.add(new ObjectListElementDto(2L, 2, "0.0.2555.255.255.255"));

        return new ObjectListTypeDto(elements);
    }

}
