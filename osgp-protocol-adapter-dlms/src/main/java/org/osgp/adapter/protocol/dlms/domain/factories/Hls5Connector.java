package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

class Hls5Connector {
    private static final String ERROR_WHILE_CREATING_TCP_CONNECTION = "Error while creating TCP connection.";

    private final int responseTimeout;
    private final int logicalDeviceAddress;
    private final int clientAccessPoint;

    private DlmsDevice device;

    private SecurityKey validAuthenticationKey;
    private SecurityKey validEncryptionKey;
    private SecurityKey neverValidAuthenticationKey;
    private SecurityKey neverValidEncryptionKey;

    private SecurityKey usedAuthenticationKey;
    private SecurityKey usedEncryptionKey;

    private LnClientConnection connection;

    public Hls5Connector(final DlmsDevice device, final int responseTimeout, final int logicalDeviceAddress,
            final int clientAccessPoint) {
        this.device = device;
        this.responseTimeout = responseTimeout;
        this.logicalDeviceAddress = logicalDeviceAddress;
        this.clientAccessPoint = clientAccessPoint;
    }

    public LnClientConnection connect() throws TechnicalException {
        this.checkIpAddress();
        this.initKeys();

        try {
            return this.createConnection();
        } catch (final UnknownHostException e) {
            // Unknown IP, unrecoverable.
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "The IP address is not found: "
                    + this.device.getIpAddress());
        } catch (final IOException e) {
            // Exception while connecting with the meter.
            // Fall back on keys that have not been marked valid.
            if (this.neverValidAuthenticationKey == null && this.neverValidEncryptionKey == null) {
                throw new ConnectionException(e.getMessage());
            }

            if (this.neverValidAuthenticationKey != null && this.neverValidEncryptionKey != null) {
                // Keys must be written to the device and stored in the database
                // one-by-one. When an error occurs on writing the first key,
                // the second should not be attempted, to keep problems to a
                // minimum.
                throw new TechnicalException(ComponentType.PROTOCOL_DLMS,
                        "Invalid state: there are multiple never-valid keys. ");
            }

            return this.createFallbackConnection();
        } finally {
            if (this.connection != null) {
                this.correctKeys();
            }
        }
    }

    public DlmsDevice getUpdatedDevice() {
        return this.device;
    }

    private void checkIpAddress() throws TechnicalException {
        if (this.device.getIpAddress() == null) {
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "Unable to get HLS5 connection for device "
                    + this.device.getDeviceIdentification() + ", because the IP address is not set.");
        }
    }

    private void initKeys() throws TechnicalException {
        this.usedAuthenticationKey = this.validAuthenticationKey = this
                .getSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION);
        this.usedEncryptionKey = this.validEncryptionKey = this.getSecurityKey(SecurityKeyType.E_METER_ENCRYPTION);

        this.neverValidAuthenticationKey = this.device.getNewSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION);
        this.neverValidEncryptionKey = this.device.getNewSecurityKey(SecurityKeyType.E_METER_ENCRYPTION);
    }

    /**
     * Switches the used key to the never valid key. Tries to switch both keys,
     * but only one keys should really be switched.
     */
    private void switchToNeverValidKey() {
        if (this.usedAuthenticationKey == this.validAuthenticationKey && this.neverValidAuthenticationKey != null) {
            this.usedAuthenticationKey = this.neverValidAuthenticationKey;
        } else {
            this.usedAuthenticationKey = this.validAuthenticationKey;
        }

        if (this.usedEncryptionKey == this.validEncryptionKey && this.neverValidEncryptionKey != null) {
            this.usedEncryptionKey = this.neverValidEncryptionKey;
        } else {
            this.usedEncryptionKey = this.validEncryptionKey;
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
    private LnClientConnection createConnection() throws IOException {
        final byte[] authenticationKey = Hex.decode(this.usedAuthenticationKey.getKey());
        final byte[] encryptionKey = Hex.decode(this.usedEncryptionKey.getKey());

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

    /**
     * Switch to the never valid keys and try to make a connection with the
     * device.
     *
     * @return The connection.
     */
    private LnClientConnection createFallbackConnection() {
        try {
            this.switchToNeverValidKey();
            return this.createConnection();
        } catch (final IOException e) {
            // Exception while connecting with the meter.
            throw new ConnectionException(ERROR_WHILE_CREATING_TCP_CONNECTION);
        }
    }

    /**
     * Makes used keys valid and removes or updates keys that are not valid
     * (anymore).
     *
     * Only executes after a connection has successfully been made, to make sure
     * the used keys are valid.
     */
    private void correctKeys() {
        this.correctKey(this.usedAuthenticationKey, this.validAuthenticationKey, this.neverValidAuthenticationKey);
        this.correctKey(this.usedEncryptionKey, this.validEncryptionKey, this.neverValidEncryptionKey);
    }

    private void correctKey(final SecurityKey used, final SecurityKey valid, final SecurityKey neverValid) {
        final Date now = new Date();

        if (used == neverValid) {
            neverValid.setValidFrom(now);
            valid.setValidTo(now);
        } else if (neverValid != null) {
            this.device.getSecurityKeys().remove(neverValid);
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
