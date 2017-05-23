package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

import org.osgp.adapter.protocol.dlms.application.services.ConfigurationService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.osgp.adapter.protocol.jasper.sessionproviders.exceptions.SessionProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SetKeysRequestDto;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Component
public class GenerateAndReplaceKeysRequestMessageProcessor extends DeviceRequestMessageProcessor {

    public static final byte[] MASTER_KEY = generateKey();
    public static final byte[] AUTHENTICATION_KEY = generateKey();
    public static final byte[] ENCRYPTION_KEY = generateKey();
    public static final int AES_GMC_128_KEY_SIZE = 128;

    @Autowired
    private ConfigurationService configurationService;

    public GenerateAndReplaceKeysRequestMessageProcessor() {
        super(DeviceRequestMessageType.GENERATE_AND_REPLACE_KEYS);
    }


    @Override
    protected Serializable handleMessage(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Serializable requestObject) throws OsgpException, ProtocolAdapterException, SessionProviderException {

        final SetKeysRequestDto keySet = new SetKeysRequestDto(AUTHENTICATION_KEY, ENCRYPTION_KEY);

        this.configurationService.generateAndReplaceKeys(conn, device, keySet);

        return null;
    }


    public static byte[] generateKey() {
        try {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_GMC_128_KEY_SIZE);
            return keyGenerator.generateKey().getEncoded();
        } catch (final NoSuchAlgorithmException e) {
            throw new AssertionError("Expected AES algorithm to be available for key generation.", e);
        }
    }
}
