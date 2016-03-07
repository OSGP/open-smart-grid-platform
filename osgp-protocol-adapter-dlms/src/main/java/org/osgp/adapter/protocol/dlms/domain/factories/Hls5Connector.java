package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.application.threads.RecoverKeyProcess;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

@Component
@Scope("prototype")
public class Hls5Connector {
    private final int responseTimeout;
    private final int logicalDeviceAddress;
    private final int clientAccessPoint;

    @Autowired
    private RecoverKeyProcess recoverKeyProcess;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private ScheduledExecutorService executorService;

    private DlmsDevice device;

    private LnClientConnection connection;

    public Hls5Connector(final int responseTimeout, final int logicalDeviceAddress, final int clientAccessPoint) {
        this.responseTimeout = responseTimeout;
        this.logicalDeviceAddress = logicalDeviceAddress;
        this.clientAccessPoint = clientAccessPoint;
    }

    public void setDevice(final DlmsDevice device) {
        this.device = device;
    }

    public LnClientConnection connect() throws TechnicalException {
        if (this.device == null) {
            throw new IllegalStateException("Can not connect because no device is set.");
        }

        this.checkIpAddress();

        try {
            final LnClientConnection connection = this.createConnection();
            this.removeInvalidKeys();
            return connection;
        } catch (final UnknownHostException e) {
            // Unknown IP, unrecoverable.
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "The IP address is not found: "
                    + this.device.getIpAddress());
        } catch (final IOException e) {
            if (!this.device.getNewSecurityKeys().isEmpty()) {
                // Queue key recovery process.
                this.recoverKeyProcess.setDeviceIdentification(this.device.getDeviceIdentification());
                this.executorService.schedule(this.recoverKeyProcess, 2, TimeUnit.MINUTES);
            }
            throw new ConnectionException(e.getMessage(), e);
        }
    }

    private void checkIpAddress() throws TechnicalException {
        if (this.device.getIpAddress() == null) {
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "Unable to get HLS5 connection for device "
                    + this.device.getDeviceIdentification() + ", because the IP address is not set.");
        }
    }

    /**
     * Create a connection with the device.
     *
     * @return The connection.
     * @throws IOException
     *             When there are problems in connecting to or communicating
     *             with the device.
     */
    private LnClientConnection createConnection() throws IOException, TechnicalException {
        final SecurityKey validAuthenticationKey = this.getSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION);
        final SecurityKey validEncryptionKey = this.getSecurityKey(SecurityKeyType.E_METER_ENCRYPTION);

        final byte[] authenticationKey = Hex.decode(validAuthenticationKey.getKey());
        final byte[] encryptionKey = Hex.decode(validEncryptionKey.getKey());

        final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(InetAddress.getByName(this.device
                .getIpAddress())).useGmacAuthentication(authenticationKey, encryptionKey)
                .enableEncryption(encryptionKey).responseTimeout(this.responseTimeout)
                .logicalDeviceAddress(this.logicalDeviceAddress).clientAccessPoint(this.clientAccessPoint);

        final Integer challengeLength = this.device.getChallengeLength();
        if (challengeLength != null) {
            tcpConnectionBuilder.challengeLength(challengeLength);
        }

        // Store the connection to use as a 'connected' flag
        this.connection = tcpConnectionBuilder.buildLnConnection();
        return this.connection;
    }

    private void removeInvalidKeys() {
        final List<SecurityKey> keys = this.device.getNewSecurityKeys();
        if (!keys.isEmpty()) {
            this.device.getSecurityKeys().removeAll(keys);
            this.device = this.dlmsDeviceRepository.save(this.device);
        }
    }

    /**
     * Get the valid securityKey of a given type for the device.
     *
     * @param securityKeyType
     * @return SecurityKey
     * @throws TechnicalException
     *             when there is no valid key of the given type.
     */
    private SecurityKey getSecurityKey(final SecurityKeyType securityKeyType) throws TechnicalException {
        final SecurityKey securityKey = this.device.getValidSecurityKey(securityKeyType);
        if (securityKey == null) {
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, String.format(
                    "There is no valid key for device '%s' of type '%s'.", this.device.getDeviceIdentification(),
                    securityKeyType.name()));
        }

        return securityKey;
    }
}
