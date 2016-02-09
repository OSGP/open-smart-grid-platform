package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.SecurityUtils;
import org.openmuc.jdlms.SecurityUtils.KeyId;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Component;

@Component
public class ReplaceKeyCommandExecutor implements
        CommandExecutor<ReplaceKeyCommandExecutor.KeyWrapper, MethodResultCode> {

    static class KeyWrapper {
        private final byte[] bytes;
        private final KeyId keyId;

        public KeyWrapper(final byte[] bytes, final KeyId keyId) {
            this.bytes = bytes;
            this.keyId = keyId;
        }

        public byte[] getBytes() {
            return this.bytes;
        }

        public KeyId getKeyId() {
            return this.keyId;
        }
    }

    public static KeyWrapper wrap(final byte[] bytes, final KeyId keyId) {
        return new KeyWrapper(bytes, keyId);
    }

    @Override
    public MethodResultCode execute(final LnClientConnection conn, final DlmsDevice device,
            final ReplaceKeyCommandExecutor.KeyWrapper object) throws IOException, TimeoutException,
            ProtocolAdapterException {

        final MethodParameter methodParameterAuth = SecurityUtils.globalKeyTransfer(this.getMasterKey(device),
                object.getBytes(), object.getKeyId());
        return conn.action(methodParameterAuth).get(0).resultCode();
    }

    /**
     * Get the valid master key from the device.
     *
     * @param device
     *            Device instance
     * @return The valid master key.
     * @throws ProtocolAdapterException
     *             when master key can not be decoded to a valid hex value.
     */
    private byte[] getMasterKey(final DlmsDevice device) throws ProtocolAdapterException {
        try {
            final SecurityKey masterKey = device.getValidSecurityKey(SecurityKeyType.E_METER_MASTER);
            return Hex.decodeHex(masterKey.getKey().toCharArray());
        } catch (final DecoderException e) {
            throw new ProtocolAdapterException("Error while decoding key hex string.", e);
        }
    }
}
