package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.openmuc.jdlms.SetRequestParameter;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDay;

@Component()
public class SetSpecialDaysCommandExecutor implements CommandExecutor<List<SpecialDay>, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationObjectCommandExecutor.class);

    private static final int CLASS_ID = 11;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.11.0.0.255");
    private static final int ATTRIBUTE_ID = 2;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final List<SpecialDay> specialDays)
            throws IOException, ProtocolAdapterException {

        final ArrayList<DataObject> specialDayEntries = new ArrayList<DataObject>();
        final int i = 0;
        for (final SpecialDay specialDay : specialDays) {

            final ArrayList<DataObject> specDayEntry = new ArrayList<DataObject>();
            specDayEntry.add(DataObject.newUInteger32Data(i));
            specDayEntry.add(this.dlmsHelperService.dateStringToOctetString(specialDay.getSpecialDayDate()));
            specDayEntry.add(DataObject.newUInteger8Data((short) i));

            final DataObject dayStruct = DataObject.newStructureData(specDayEntry);
            specialDayEntries.add(dayStruct);
        }

        final DataObject arrayData = DataObject.newArrayData(specialDayEntries);

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final SetRequestParameter request = factory.createSetRequestParameter(arrayData);

        return conn.set(request).get(0);
    }

}
