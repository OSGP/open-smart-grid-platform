/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDay;

@Component()
public class SetSpecialDaysCommandExecutor implements CommandExecutor<List<SpecialDay>, AccessResultCode> {

    private static final int CLASS_ID = 11;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.11.0.0.255");
    private static final int ATTRIBUTE_ID = 2;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final DlmsDevice device,
            final List<SpecialDay> specialDays) throws ProtocolAdapterException {

        final List<DataObject> specialDayEntries = new ArrayList<DataObject>();
        int i = 0;
        for (final SpecialDay specialDay : specialDays) {

            final List<DataObject> specDayEntry = new ArrayList<DataObject>();
            specDayEntry.add(DataObject.newUInteger16Data(i));
            specDayEntry.add(this.dlmsHelperService.dateStringToOctetString(specialDay.getSpecialDayDate()));
            specDayEntry.add(DataObject.newUInteger8Data((short) specialDay.getDayId()));

            final DataObject dayStruct = DataObject.newStructureData(specDayEntry);
            specialDayEntries.add(dayStruct);
            i += 1;
        }

        final AttributeAddress specialDaysTableEntries = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final DataObject entries = DataObject.newArrayData(specialDayEntries);

        final SetParameter request = new SetParameter(specialDaysTableEntries, entries);

        try {
            return conn.set(request).get(0);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

}
