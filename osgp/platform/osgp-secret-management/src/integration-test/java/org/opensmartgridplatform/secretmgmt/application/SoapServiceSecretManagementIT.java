package org.opensmartgridplatform.secretmgmt.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretTypes;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.opensmartgridplatform.secretmgmt.serviceclient.SoapConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Source;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.ws.test.client.RequestMatchers.payload;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) //needed to get a running webserver
@SpringBootTest
@AutoConfigureTestEntityManager
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SoapServiceSecretManagementIT {

    private static final String E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_SOAP = "74efc062231e81c9e006bb56c5dec38631210c5073511606a203ba748fcdc794";
    private static final String E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_DB   = "35c6d2af323bd3c4a588692dfcf4235fd20c2bd39bcf8672b6e65d515940150f";

    private static final String E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_SOAP = "3dca51832c70e372460796ca01acbab769fd330c9b936246a01d4e97f8c5bc26";
    private static final String E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_DB   = "7c737a402bdef7a0819f47ae9b625e2d8531e6c5d7603c4e4982c45175c4e063";

    private static final String DEVICE_IDENTIFICATION="E0054002019112319";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SoapConnector soapClient;

    private MockWebServiceServer mockServer;

    @BeforeEach
    public void createData() {
        createTestData();
        mockServer = MockWebServiceServer.createServer(soapClient);
    }

    //@Test
    public void testGetSecretsRequest() {
        GetSecretsRequest request = createGetSecretsRequest();

        try {
            GetSecretsResponse response = (GetSecretsResponse) soapClient.callWebService("http://localhost:8080/ws/SecretManagement", request);
            assertThat(response).isNotNull();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void testGetSecretsWithMockServer() {

        GetSecretsRequest request = new GetSecretsRequest();

        request.setDeviceId(DEVICE_IDENTIFICATION);

        Source expectedRequestPayload =
                new StringSource(
                        "<ns2:getSecretsRequest xmlns:ns2='http://www.opensmartgridplatform.org/schemas/security/secretmanagement/2020/05'>"+
                                            "<DeviceId>E0054002019112319</DeviceId>"+
                                            "<SecretTypes>"+
                                                "<SecretType>E_METER_AUTHENTICATION_KEY</SecretType>"+
                                                "<SecretType>E_METER_ENCRYPTION_KEY_UNICAST</SecretType>"+
                                            "</SecretTypes>"+
                                          "</ns2:getSecretsRequest>"
                );

        Source responsePayload = new StringSource("<getSecretsResponse>"+
                                                        "</getSecretsResponse>");

        mockServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));

        try {
            GetSecretsResponse response = (GetSecretsResponse) soapClient.callWebService("http://localhost:8080/ws/SecretManagement", request);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }

        mockServer.verify();

    }

    private GetSecretsRequest createGetSecretsRequest() {
        GetSecretsRequest request = new GetSecretsRequest();

        //build request message structure
        SecretTypes secretTypesToGet = new SecretTypes();
        List<SecretType> secretTypeList = secretTypesToGet.getSecretType();
        request.setSecretTypes(secretTypesToGet);

        //add test details
        request.setDeviceId(DEVICE_IDENTIFICATION);

        //add key types to get
        secretTypeList.add(SecretType.E_METER_AUTHENTICATION_KEY);
        secretTypeList.add(SecretType.E_METER_ENCRYPTION_KEY_UNICAST);

        return request;
    }

    /**
     * Create test data for encrypted secrets and related encryptionkey reference(s).
     * So that the EncryptionService can encrypt and decrypt, using the JRE encryption provider.
     *
     * Two secrets (for two types of meter key secrets) and one reference key (valid as of now-1minute) is created.
     */
    public void createTestData() {
        DbEncryptionKeyReference encryptionKey = new DbEncryptionKeyReference();
        encryptionKey.setCreationTime(new Date());
        encryptionKey.setReference("1");
        encryptionKey.setEncryptionProviderType(EncryptionProviderType.JRE);
        encryptionKey.setValidFrom(new Date(System.currentTimeMillis()-60000));
        encryptionKey.setVersion(1L);
        //encryptionKey = this.entityManager.persist(encryptionKey);

        DbEncryptedSecret encryptedSecret = new DbEncryptedSecret();
        encryptedSecret.setDeviceIdentification(DEVICE_IDENTIFICATION);
        encryptedSecret.setSecretType(org.opensmartgridplatform.secretmgmt.application.domain.SecretType.E_METER_AUTHENTICATION_KEY);
        encryptedSecret.setEncodedSecret(E_METER_AUTHENTICATION_KEY_ENCRYPTED_FOR_DB);
        encryptedSecret.setEncryptionKeyReference(encryptionKey);

        //encryptedSecret = this.entityManager.persist(encryptedSecret);

        DbEncryptedSecret encryptedSecret2 = new DbEncryptedSecret();
        encryptedSecret2.setDeviceIdentification(DEVICE_IDENTIFICATION);
        encryptedSecret2.setSecretType(org.opensmartgridplatform.secretmgmt.application.domain.SecretType.E_METER_ENCRYPTION_KEY_UNICAST);
        encryptedSecret2.setEncodedSecret(E_METER_ENCRYPTION_KEY_UNICAST_ENCRYPTED_FOR_DB);
        encryptedSecret2.setEncryptionKeyReference(encryptionKey);

        //encryptedSecret2 = this.entityManager.persist(encryptedSecret2);

        //this.entityManager.flush();


    }
}
