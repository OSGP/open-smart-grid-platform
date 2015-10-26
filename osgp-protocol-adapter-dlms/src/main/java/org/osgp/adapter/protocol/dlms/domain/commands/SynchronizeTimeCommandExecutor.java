package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.openmuc.jdlms.SetRequestParameter;
import org.springframework.stereotype.Component;

@Component()
public class SynchronizeTimeCommandExecutor implements CommandExecutor {

    private static final int CLASS_ID = 8;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.1.0.0.255");
    private static final int ATTRIBUTE_ID = 2;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final DataObject object) throws IOException {
        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        final ByteBuffer bb = ByteBuffer.allocate(12);
        final DateTime dt = DateTime.now();

        // DLMS DATE TIME DEFINITION:
        // OCTET STRING (SIZE(12))
        // {
        // year highbyte,
        // year lowbyte,
        // month,
        // day of month,
        // day of week,
        // hour,
        // minute,
        // second,
        // hundredths of second,
        // deviation highbyte,
        // deviation lowbyte,
        // clock status
        // }

        bb.putShort((short) dt.getYear());
        bb.put((byte) dt.getMonthOfYear());
        bb.put((byte) dt.getDayOfMonth());
        bb.put((byte) 0xFF);
        bb.put((byte) dt.getHourOfDay());
        bb.put((byte) dt.getMinuteOfHour());
        bb.put((byte) dt.getSecondOfMinute());
        bb.put((byte) 0xFF);
        bb.put((byte) 0x80);
        bb.put((byte) 0x00);
        bb.put((byte) 128);

        final DataObject obj = DataObject.newOctetStringData(bb.array());

        final SetRequestParameter request = factory.createSetRequestParameter(obj);

        return conn.set(request).get(0);
    }
}
