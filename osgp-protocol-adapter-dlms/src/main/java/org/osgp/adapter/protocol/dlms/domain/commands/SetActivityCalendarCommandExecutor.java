package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetActivityCalendarCommandExecutor implements CommandExecutor<String, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetActivityCalendarCommandExecutor.class);

    @Override
    public AccessResultCode execute(final ClientConnection conn, final String activityCalendarName) throws IOException {
        LOGGER.debug("SetActivityCalendarCommandExecutor.execute {} called!! :-)", activityCalendarName);
        return AccessResultCode.SUCCESS;
    }
}
