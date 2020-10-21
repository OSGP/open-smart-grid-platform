package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.application.wsclient.SecretManagementClient;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.opensmartgridplatform.ws.schema.core.secret.management.ActivateSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.GenerateAndStoreSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.GetSecretsResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.HasNewSecretResponse;
import org.opensmartgridplatform.ws.schema.core.secret.management.OsgpResultType;
import org.opensmartgridplatform.ws.schema.core.secret.management.StoreSecretsRequest;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecrets;

@ExtendWith(MockitoExtension.class)
public class SecretManagementServiceTest {
    @InjectMocks
    SecretManagementService testService;

    @Mock
    SecretManagementClient secretManagementClient;
    @Mock
    RsaEncrypter rsaEncrypter;

    private static final String DEVICE_IDENTIFICATION = "E000123456789";
    private static final SecurityKeyType KEY_TYPE = SecurityKeyType.E_METER_ENCRYPTION;
    private static final byte[] UNENCRYPTED_SECRET = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
    private static final byte[] AES_SECRET = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    private static final byte[] SOAP_SECRET = {15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0};
    private static final String HEX_SOAP_SECRET = Hex.encodeHexString(SOAP_SECRET);
    private static final TypedSecret TYPED_SECRET = new TypedSecret();

    @BeforeAll
    public static void init() {
        TYPED_SECRET.setType(KEY_TYPE.toSecretType());
        TYPED_SECRET.setSecret(HEX_SOAP_SECRET);
    }

    @Test
    public void testGetKeys() {
        //SETUP
        List<SecurityKeyType> keyTypes = Arrays.asList(KEY_TYPE);
        GetSecretsResponse response = new GetSecretsResponse();
        response.setResult(OsgpResultType.OK);
        response.setTypedSecrets(new TypedSecrets());
        response.getTypedSecrets().getTypedSecret().add(TYPED_SECRET);
        when(this.secretManagementClient.getSecretsRequest(any())).thenReturn(response);
        when(this.rsaEncrypter.decrypt(SOAP_SECRET)).thenReturn(UNENCRYPTED_SECRET);
        //EXECUTE
        Map<SecurityKeyType,byte[]> result = this.testService.getKeys(DEVICE_IDENTIFICATION,keyTypes);
        //ASSERT
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.keySet().iterator().next()).isEqualTo(KEY_TYPE);
        assertThat(result.values().iterator().next()).isEqualTo(UNENCRYPTED_SECRET);
    }

    @Test
    public void testStoreNewKeys() {
        Map<SecurityKeyType,byte[]> keys = new HashMap<>();
        keys.put(KEY_TYPE,UNENCRYPTED_SECRET);
        when(this.rsaEncrypter.encrypt(UNENCRYPTED_SECRET)).thenReturn(SOAP_SECRET);
        ArgumentCaptor<StoreSecretsRequest> storeSecretsCaptor =
                ArgumentCaptor.forClass(StoreSecretsRequest.class);
        //EXECUTE
        this.testService.storeNewKeys(DEVICE_IDENTIFICATION, keys);
        //ASSERT
        verify(this.secretManagementClient).storeSecretsRequest(storeSecretsCaptor.capture());
        StoreSecretsRequest capturedArgument = storeSecretsCaptor.getValue();
        assertThat(capturedArgument.getDeviceId()).isEqualTo(DEVICE_IDENTIFICATION);
        assertThat(capturedArgument.getTypedSecrets().getTypedSecret().get(0).getSecret()).isEqualTo(HEX_SOAP_SECRET);
    }

    @Test
    public void testActivateKeys() throws ProtocolAdapterException {
        List<SecurityKeyType> keyTypes = Arrays.asList(KEY_TYPE);
        ArgumentCaptor<ActivateSecretsRequest> activateSecretsCaptor =
                ArgumentCaptor.forClass(ActivateSecretsRequest.class);
        //EXECUTE
        this.testService.activateNewKeys(DEVICE_IDENTIFICATION, keyTypes);
        //ASSERT
        verify(this.secretManagementClient).activateSecretsRequest(activateSecretsCaptor.capture());
        ActivateSecretsRequest capturedArgument = activateSecretsCaptor.getValue();
        assertThat(capturedArgument.getDeviceId()).isEqualTo(DEVICE_IDENTIFICATION);
        assertThat(capturedArgument.getSecretTypes().getSecretType().get(0)).isEqualTo(KEY_TYPE.toSecretType());
    }

    @Test
    public void testGenerateAndStoreKeys() {
        List<SecurityKeyType> keyTypes = Arrays.asList(KEY_TYPE);
        GenerateAndStoreSecretsResponse response = new GenerateAndStoreSecretsResponse();
        response.setResult(OsgpResultType.OK);
        response.setTypedSecrets(new TypedSecrets());
        response.getTypedSecrets().getTypedSecret().add(TYPED_SECRET);
        when(this.secretManagementClient.generateAndStoreSecrets(any())).thenReturn(response);
        when(this.rsaEncrypter.decrypt(SOAP_SECRET)).thenReturn(UNENCRYPTED_SECRET);
        //EXECUTE
        Map<SecurityKeyType,byte[]> keys = this.testService.generate128BitsKeysAndStoreAsNewKeys(DEVICE_IDENTIFICATION,
                keyTypes);
        //ASSERT
        assertThat(keys.get(KEY_TYPE)).isEqualTo(UNENCRYPTED_SECRET);
    }

    @Test
    public void testHasNewKey() {
        HasNewSecretResponse response = new HasNewSecretResponse();
        response.setHasNewSecret(true);
        when(this.secretManagementClient.hasNewSecretRequest(any())).thenReturn(response);
        //EXECUTE
        boolean result = this.testService.hasNewSecretOfType(DEVICE_IDENTIFICATION, KEY_TYPE);
        //ASSERT
        assertThat(result).isTrue();
    }
}
