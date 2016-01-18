package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;

import javax.naming.OperationNotSupportedException;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.springframework.stereotype.Component;

@Component
public class DlmsConnectionFactory {

    // TODO REPLACE BY CONFIGURATION PROPERTIES
    private final static int W_PORT_SOURCE = 1;
    private final static int W_PORT_DESTINATION = 1;
    private final static int RESPONSE_TIMEOUT = 60000;

    /**
     * Returns an open connection using the appropriate security settings for
     * the device
     *
     * @param device
     * @return an open connection
     * @throws IOException
     * @throws OperationNotSupportedException
     */
    public LnClientConnection getConnection(final DlmsDevice device) throws IOException, OperationNotSupportedException {

        if (device.isHls5Active()) {
            return this.getHls5Connection(device);
        } else {
            // TODO ADD IMPLEMENTATIONS FOR OTHER SECURITY MODES
            throw new OperationNotSupportedException("Only HLS 5 connections are currently supported");
        }
    }

    private LnClientConnection getHls5Connection(final DlmsDevice device) throws IOException {

        final byte[] authenticationKey = Hex.decode(device.getAuthenticationKey());
        final byte[] encryptionKey = Hex.decode(device.getGlobalEncryptionUnicastKey());

        final String ipAddress = device.getIpAddress();
        if (ipAddress == null) {
            throw new IOException("Unable to get HLS5 connection for device " + device.getDeviceIdentification()
                    + ", because the IP address is not set.");
        }
        final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(InetAddress.getByName(ipAddress))
                .useGmacAuthentication(authenticationKey, encryptionKey).enableEncryption(encryptionKey)
                .responseTimeout(RESPONSE_TIMEOUT).logicalDeviceAddress(W_PORT_DESTINATION)
                .clientAccessPoint(W_PORT_SOURCE);

        final Integer challengeLength = device.getChallengeLength();
        if (challengeLength != null) {
            tcpConnectionBuilder.challengeLength(challengeLength);
        }

        return tcpConnectionBuilder.buildLnConnection();
    }
}
