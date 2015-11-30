package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotification;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest;

@Component
public class ReadAlarmRegisterCommandExecutor implements CommandExecutor<ReadAlarmRegisterRequest, AlarmNotifications> {

    @Override
    public AlarmNotifications execute(final ClientConnection conn, final ReadAlarmRegisterRequest object)
            throws IOException, ProtocolAdapterException {

        // Mock a return value for read alarm register
        final Set<AlarmNotification> notifications = new HashSet<>();
        notifications.add(new AlarmNotification(AlarmType.CLOCK_INVALID, true));

        final AlarmNotifications alarmNotifications = new AlarmNotifications(notifications);

        return alarmNotifications;
    }

}
