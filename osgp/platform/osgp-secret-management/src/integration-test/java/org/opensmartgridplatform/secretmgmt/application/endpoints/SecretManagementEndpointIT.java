package org.opensmartgridplatform.secretmgmt.application.endpoints;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.Caller;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretTypes;
import org.opensmartgridplatform.secretmgmt.application.services.SecretManagementService;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecretManagementEndpointIT {

    @Autowired
    private SecretManagementEndpoint secretManagementEndpoint;

    @Autowired
    private SecretManagementService secretManagementService;

    @Autowired
    private EncryptionProvider soapSecretEncryptionProvider;

    //@BeforeAll

    @BeforeEach
    public void beforeEachTest() {
        secretManagementEndpoint = new SecretManagementEndpoint(secretManagementService, soapSecretEncryptionProvider);
    }

    @Test
    public void getSecretsBySoapCall() {
        GetSecretsRequest request = new GetSecretsRequest();
        Caller caller = new Caller();
        caller.setUserName("soap-api-user");
        caller.setOrganisationIdentification("soap-user-org");
        caller.setApplicationName("TEST");
        request.setCaller(caller);

        SecretTypes secretTypesToGet = new SecretTypes();

        List<SecretType> secretTypeList = secretTypesToGet.getSecretType();

        secretTypeList.add(SecretType.E_METER_AUTHENTICATION_KEY);
        secretTypeList.add(SecretType.E_METER_ENCRYPTION_KEY_UNICAST);

        request.setSecretTypes(secretTypesToGet);
        request.setCorrelationUid("1234");
        request.setDeviceId("E123");
        GetSecretsResponse response = secretManagementEndpoint.getSecretsRequest(request);

    }
}
