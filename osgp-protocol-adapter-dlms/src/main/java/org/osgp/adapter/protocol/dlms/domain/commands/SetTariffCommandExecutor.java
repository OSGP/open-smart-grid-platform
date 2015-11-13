package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;

import org.jboss.logging.Logger;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.springframework.stereotype.Component;

@Component()
public class SetTariffCommandExecutor implements CommandExecutor<String> {

    private Logger logger = Logger.getLogger(SetTariffCommandExecutor.class);

    @Override
    public AccessResultCode execute(final ClientConnection conn, final String tariff) throws IOException {
        this.logger.debug(String.format("SetTariffCommandExecutor.execute <%s>called!! :-)", tariff));
        return AccessResultCode.SUCCESS;
    }
}
