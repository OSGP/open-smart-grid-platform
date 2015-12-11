package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;

import javax.naming.OperationNotSupportedException;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.ClientSap;
import org.openmuc.jdlms.TcpClientSap;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.springframework.stereotype.Component;

@Component
public class DlmsConnectionFactory {

    // TODO REPLACE BY CONFIGURATION PROPERTIES
    private final static int W_PORT_SOURCE = 1;
    private final static int W_PORT_DESTINATION = 1;
    private final static boolean LN_REFERENCING_ENABLED = true;
    private final static int RESPONSE_TIMEOUT = 60000;

    // TODO REPLACE HARD-CODED IP-ADDRESS!!!
    private final static String REMOTE_HOST = "89.200.96.223";

    /**
     * Returns an open connection using the appropriate security settings for the device
     *
     * @param device
     * @return an open connection
     * @throws IOException
     * @throws OperationNotSupportedException
     */
    public ClientConnection getConnection(final DlmsDevice device) throws IOException, OperationNotSupportedException {

        if (device.isHLS5Active()) {
            return this.getHls5Connection(device);
        } else {
            // TODO ADD IMPLEMENTATIONS FOR OTHER SECURITY MODES
            throw new OperationNotSupportedException("Only HLS 5 connections are currently supported");
        }
    }

    private ClientConnection getHls5Connection(final DlmsDevice device) throws IOException {

        final byte[] authenticationKey = Hex.decode(device.getAuthenticationKey());
        final byte[] encryptionKey = Hex.decode(device.getGlobalEncryptionUnicastKey()); 

        final ClientSap clientSap = new TcpClientSap(InetAddress.getByName(REMOTE_HOST));
        clientSap.enableGmacAuthentication(authenticationKey, encryptionKey);
        clientSap.enableEncryption(encryptionKey);
        clientSap.setResponseTimeout(RESPONSE_TIMEOUT);

        return clientSap.setLogicalDeviceAddress(W_PORT_DESTINATION).setClientAccessPoint(W_PORT_SOURCE)
                .setLogicalNameReferencingEnabled(LN_REFERENCING_ENABLED).connect();
    }
}
