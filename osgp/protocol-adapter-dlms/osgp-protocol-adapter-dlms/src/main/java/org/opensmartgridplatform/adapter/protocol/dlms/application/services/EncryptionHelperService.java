package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.security.RsaEncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncryptionHelperService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionHelperService.class);

    @Autowired
    private RsaEncryptionService rsaEncryptionService;

    public byte[] rsaDecrypt(final byte[] externallyEncryptedKey) throws FunctionalException {
        try {
            return this.rsaEncryptionService.decrypt(externallyEncryptedKey);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during decryption", e);
            throw new FunctionalException(FunctionalExceptionType.DECRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS, e);
        }
    }
}
