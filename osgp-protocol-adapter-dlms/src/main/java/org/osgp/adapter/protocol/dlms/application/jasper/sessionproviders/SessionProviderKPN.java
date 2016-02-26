package org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders;

import javax.annotation.PostConstruct;

import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.adapter.protocol.dlms.infra.ws.JasperWirelessTerminalClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jasperwireless.api.ws.service.sms.GetSessionInfoResponse;
import com.jasperwireless.api.ws.service.sms.SessionInfoType;

@Component
public class SessionProviderKPN extends SessionProvider {

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
    public String getIpAddress(final String iccId) throws SessionProviderException {

        final GetSessionInfoResponse response = this.jasperWirelessTerminalClient.pollGetSession(iccId);

        final SessionInfoType sessionInfoType = this.getSessionInfo(response);

        if (sessionInfoType == null) {
            return null;
        }
        return sessionInfoType.getIpAddress();
    }

    private SessionInfoType getSessionInfo(final GetSessionInfoResponse response) throws SessionProviderException {
        if (response == null || response.getSessionInfo() == null || response.getSessionInfo().getSession() == null) {
            throw new SessionProviderException("Response Object is not ok: " + response);
        }
        if (response.getSessionInfo().getSession().size() == 1) {
            return response.getSessionInfo().getSession().get(0);
        }
        return null;

    }

}
