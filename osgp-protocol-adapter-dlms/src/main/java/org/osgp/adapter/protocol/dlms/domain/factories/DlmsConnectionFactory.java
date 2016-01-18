package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;

import javax.naming.OperationNotSupportedException;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.SecurityKeyRepository;
import org.osgp.adapter.protocol.dlms.exceptions.DlmsConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DlmsConnectionFactory {

    // TODO REPLACE BY CONFIGURATION PROPERTIES
    private final static int W_PORT_SOURCE = 1;
    private final static int W_PORT_DESTINATION = 1;
    private final static int RESPONSE_TIMEOUT = 60000;

    @Autowired
    private SecurityKeyRepository securityKeyRepository;

    /**
     * Returns an open connection using the appropriate security settings for
     * the device
     *
     * @param device
     * @return an open connection
     * @throws IOException
     * @throws OperationNotSupportedException
     */
    public LnClientConnection getConnection(final DlmsDevice device) throws DlmsConnectionException,
    OperationNotSupportedException {

        if (device.isHls5Active()) {
            return this.getHls5Connection(device);
        } else {
            // TODO ADD IMPLEMENTATIONS FOR OTHER SECURITY MODES
            throw new OperationNotSupportedException("Only HLS 5 connections are currently supported");
        }
    }

    private LnClientConnection getHls5Connection(final DlmsDevice device) throws DlmsConnectionException {

        final byte[] authenticationKey = this.getSecurityKey(device, SecurityKeyType.E_METER_AUTHENTICATION);
        final byte[] encryptionKey = this.getSecurityKey(device, SecurityKeyType.E_METER_ENCRYPTION);

        final String ipAddress = device.getIpAddress();
        if (ipAddress == null) {
            throw new DlmsConnectionException("Unable to get HLS5 connection for device "
                    + device.getDeviceIdentification() + ", because the IP address is not set.");
        }

        try {
            final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(InetAddress.getByName(ipAddress))
                .useGmacAuthentication(authenticationKey, encryptionKey).enableEncryption(encryptionKey)
                .responseTimeout(RESPONSE_TIMEOUT).logicalDeviceAddress(W_PORT_DESTINATION)
                .clientAccessPoint(W_PORT_SOURCE);

        final Integer challengeLength = device.getChallengeLength();
        if (challengeLength != null) {
            tcpConnectionBuilder.challengeLength(challengeLength);
        }

        return tcpConnectionBuilder.buildLnConnection();
        } catch (final IOException e) {
            throw new DlmsConnectionException("Error while creating TCP connection.", e);
        }
    }

    /**
     * Get the valid security of a given type for the device.
     *
     * @param dlmsDevice
     * @param securityKeyType
     * @return Byte array containing the security key.
     * @throws DlmsConnectionException
     *             when there is no valid key.
     */
    private byte[] getSecurityKey(final DlmsDevice dlmsDevice, final SecurityKeyType securityKeyType)
            throws DlmsConnectionException {
        final SecurityKey securityKey = this.securityKeyRepository.findValidSecurityKey(dlmsDevice, securityKeyType);
        if (securityKey == null) {
            throw new DlmsConnectionException(String.format("There is no valid key for device '%s' of type '%s'.",
                    dlmsDevice.getDeviceIdentification(), securityKeyType.name()));
        }

        return Hex.decode(securityKey.getSecurityKey());
    }
}
