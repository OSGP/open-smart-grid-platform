package org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class SessionProvider {

    @Autowired
    protected SessionProviderMap sessionProviderMap;

    public abstract String getIpAddress(String iccId) throws SessionProviderException;
}
