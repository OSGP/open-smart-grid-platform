package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

public class SecretMangementKeyService implements ISecurityKeyService {

    @Override
    public byte[] getDlmsAuthenticationKey(String deviceIdentification) {
        return new byte[0];
    }

    @Override
    public byte[] getDlmsGlobalUnicastEncryptionKey(String deviceIdentification) {
        return new byte[0];
    }
}
