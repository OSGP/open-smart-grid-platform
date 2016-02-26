package org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders;

import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.exceptions.SessionProviderException;
import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.exceptions.SessionProviderUnsupportedException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SessionProvider {

    @Autowired
    protected SessionProviderMap sessionProviderMap;

    public abstract String getIpAddress(String iccId) throws SessionProviderException,
            SessionProviderUnsupportedException;
}
