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

import com.alliander.osgp.dto.valueobjects.smartmetering.KeySet;

@Component
public class ReplaceKeysCommandExecutor implements CommandExecutor<KeySet, MethodResultCode> {

    @Override
    public MethodResultCode execute(final LnClientConnection conn, final DlmsDevice device, final KeySet object)
            throws IOException, TimeoutException, ProtocolAdapterException {

        final byte[] masterKey = this.getStoredKey(device, SecurityKeyType.E_METER_MASTER);

        // Change authentication key
        final MethodResultCode authenticationKeyResult = this.replaceKey(conn, masterKey,
                object.getAuthenticationKey(), KeyId.AUTHENTICATION_KEY);
        if (!MethodResultCode.SUCCESS.equals(authenticationKeyResult)) {
            return authenticationKeyResult;
        }

        // Change encryption key.
        final MethodResultCode encryptionKeyResult = this.replaceKey(conn, masterKey, object.getEncryptionKey(),
                KeyId.GLOBAL_UNICAST_ENCRYPTION_KEY);
        if (!MethodResultCode.SUCCESS.equals(encryptionKeyResult)) {
            // Setting of encryption key failed. As the method should replace
            // both keys or none, it should try to revert changes in
            // authentication key. It is not guaranteed this will work though.
            final byte[] originalAuthenticationKey = this.getStoredKey(device, SecurityKeyType.E_METER_AUTHENTICATION);
            this.replaceKey(conn, masterKey, originalAuthenticationKey, KeyId.AUTHENTICATION_KEY);
        }

        return encryptionKeyResult;
    }

    private byte[] getStoredKey(final DlmsDevice device, final SecurityKeyType type) throws ProtocolAdapterException {
        try {
            final SecurityKey masterKey = device.getValidSecurityKey(type);
            return Hex.decodeHex(masterKey.getKey().toCharArray());
        } catch (final DecoderException e) {
            throw new ProtocolAdapterException("Error while decoding key hex string.", e);
        }
    }

    private MethodResultCode replaceKey(final LnClientConnection conn, final byte[] masterKey, final byte[] newKey,
            final KeyId keyId) throws IOException {
        final MethodParameter methodParameterAuth = SecurityUtils.globalKeyTransfer(masterKey, newKey, keyId);
        return conn.action(methodParameterAuth).get(0).resultCode();
    }
}
