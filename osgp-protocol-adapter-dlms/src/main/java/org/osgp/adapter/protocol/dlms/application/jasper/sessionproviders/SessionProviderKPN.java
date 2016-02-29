package org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders;

import javax.annotation.PostConstruct;

import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.exceptions.SessionProviderException;
import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.exceptions.SessionProviderUnsupportedException;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.adapter.protocol.dlms.infra.ws.JasperWirelessTerminalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.jasperwireless.api.ws.service.GetSessionInfoResponse;
import com.jasperwireless.api.ws.service.SessionInfoType;

@Component
public class SessionProviderKPN extends SessionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionProviderKPN.class);

    @Autowired
    JasperWirelessTerminalClient jasperWirelessTerminalClient;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    /**
     * Initialization function executed after dependency injection has finished.
     * The SessionProvider Singleton is added to the HashMap of
     * SessionProviderMap. The key for the HashMap is the string value of the
     * enumeration member.
     */
    @PostConstruct
    public void init() {
        this.sessionProviderMap.addProvider(SessionProviderEnum.KPN, this);
    }

    @Override
    public String getIpAddress(final String iccId) throws SessionProviderException, SessionProviderUnsupportedException {
        GetSessionInfoResponse response = null;
        try {
            response = this.jasperWirelessTerminalClient.getSession(iccId);
        } catch (final SoapFaultClientException e) {
            final String errorMessage = String.format("iccId %s is probably not supported in this session provider",
                    iccId);
            LOGGER.warn(errorMessage);
            throw new SessionProviderUnsupportedException(errorMessage, e);
        }

        final SessionInfoType sessionInfoType = this.getSessionInfo(response);

        if (sessionInfoType == null) {
            return null;
        }
        return sessionInfoType.getIpAddress();
    }

    private SessionInfoType getSessionInfo(final GetSessionInfoResponse response) throws SessionProviderException {
        if (response == null || response.getSessionInfo() == null || response.getSessionInfo().getSession() == null) {
            final String errorMessage = String.format("Response Object is not ok: %s" + response);
            LOGGER.warn(errorMessage);
            throw new SessionProviderException(errorMessage);
        }
        if (response.getSessionInfo().getSession().size() == 1) {
            return response.getSessionInfo().getSession().get(0);
        }
        return null;

    }

}
