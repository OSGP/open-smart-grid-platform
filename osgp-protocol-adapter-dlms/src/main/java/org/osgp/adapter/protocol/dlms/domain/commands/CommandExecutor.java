package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;

public interface CommandExecutor {

    AccessResultCode execute(ClientConnection conn, DataObject object) throws IOException;

}
