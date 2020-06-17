package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

public interface ISecurityKeyService {

    byte[] getDlmsAuthenticationKey(final String deviceIdentification);
    byte[] getDlmsGlobalUnicastEncryptionKey(final String deviceIdentification);

}
