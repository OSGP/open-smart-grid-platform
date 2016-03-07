package org.osgp.adapter.protocol.dlms.application.threads;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class RecoverKeyProcess implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecoverKeyProcess.class);

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    private String deviceIdentification;

    private DlmsDevice device;

    private String ipAddress;

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        if (this.deviceIdentification == null) {
            throw new IllegalStateException("DeviceIdentification not set.");
        }
        if (this.ipAddress == null) {
            throw new IllegalStateException("IP address not set.");
        }

        LOGGER.info("Attempting key recovery for device {}", this.deviceIdentification);

        this.device = this.dlmsDeviceRepository.findByDeviceIdentification(this.deviceIdentification);
        if (this.device == null) {
            throw new IllegalArgumentException("Device " + this.deviceIdentification + " not found.");
        }
        this.device.setIpAddress(this.ipAddress);

        if (this.device.getNewSecurityKeys().isEmpty()) {
            return;
        }

        if (this.canConnect()) {
            this.makeKeysValid();
        }
    }

    private boolean canConnect() {
        LnClientConnection connection = null;
        try {
            connection = this.createConnection();
            return true;
        } catch (final Exception e) {
            LOGGER.warn("Connection exception: {}", e.getMessage(), e);
            return false;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private void makeKeysValid() {
        final Date now = new Date();

        final SecurityKey auth = this.getSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION);
        if (auth.getValidFrom() == null) {
            this.device.getValidSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION).setValidTo(now);
            auth.setValidFrom(now);
        }

        final SecurityKey enc = this.getSecurityKey(SecurityKeyType.E_METER_ENCRYPTION);
        if (enc.getValidFrom() == null) {
            this.device.getValidSecurityKey(SecurityKeyType.E_METER_ENCRYPTION).setValidTo(now);
            enc.setValidFrom(now);
        }

        this.dlmsDeviceRepository.save(this.device);
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
        final byte[] authenticationKey = Hex.decode(this.getSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION)
                .getKey());
        final byte[] encryptionKey = Hex.decode(this.getSecurityKey(SecurityKeyType.E_METER_ENCRYPTION).getKey());

        LOGGER.info("Keys: {} = {}", this.getSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION).getKey(), this
                .getSecurityKey(SecurityKeyType.E_METER_ENCRYPTION).getKey());

        final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(InetAddress.getByName(this.device
                .getIpAddress())).useGmacAuthentication(authenticationKey, encryptionKey)
                .enableEncryption(encryptionKey).responseTimeout(10000).logicalDeviceAddress(1).clientAccessPoint(1);

        final Integer challengeLength = this.device.getChallengeLength();
        if (challengeLength != null) {
            tcpConnectionBuilder.challengeLength(challengeLength);
        }

        return tcpConnectionBuilder.buildLnConnection();
    }

    private SecurityKey getSecurityKey(final SecurityKeyType securityKeyType) {
        SecurityKey key = this.device.getNewSecurityKey(securityKeyType);
        if (key == null) {
            key = this.device.getValidSecurityKey(securityKeyType);
        }
        return key;
    }
}
