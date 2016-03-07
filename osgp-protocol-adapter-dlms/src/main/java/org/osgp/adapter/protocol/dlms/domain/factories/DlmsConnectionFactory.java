package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;

import javax.inject.Provider;
import javax.naming.OperationNotSupportedException;

import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

@Component
public class DlmsConnectionFactory {

    @Autowired
    private Provider<Hls5Connector> hls5ConnectorProvider;

    public DlmsConnectionFactory() {

    }

    /**
     * Returns an open connection using the appropriate security settings for
     * the device.
     *
     * @param device
     *            The device to connect to. This reference can be updated when
     *            the invalid but correctable connection credentials are
     *            detected.
     * @return an open connection
     * @throws IOException
     * @throws OperationNotSupportedException
     */
    public LnClientConnection getConnection(final DlmsDevice device) throws TechnicalException {
        if (device.isHls5Active()) {
            final Hls5Connector connector = this.hls5ConnectorProvider.get();
            connector.setDevice(device);
            return connector.connect();
        } else {
            // TODO ADD IMPLEMENTATIONS FOR OTHER SECURITY MODES
            throw new UnsupportedOperationException("Only HLS 5 connections are currently supported");
        }
    }
}
