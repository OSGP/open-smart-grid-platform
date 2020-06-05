package org.opensmartgridplatform.secretmgmt.application.endpoints;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.Caller;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretTypes;
import org.opensmartgridplatform.secretmgmt.application.services.SecretManagementService;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProvider;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SecretManagementEndpointTest {

    SecretManagementEndpoint instance;

    @Mock
    private SecretManagementService secretManagementService;

    @Mock
    private EncryptionProvider soapSecretEncryptionProvider;

    @BeforeEach
    public void setUp() {
        this.instance = new SecretManagementEndpoint(this.secretManagementService, this.soapSecretEncryptionProvider);
    }

    @Test
    public void doTest() {

        GetSecretsRequest getSecretsRequest = new GetSecretsRequest();

        Caller caller = new Caller();
        caller.setApplicationName("applicationName");
        caller.setOrganisationIdentification("ORG1");
        caller.setUserName("system");

        getSecretsRequest.setCaller(caller);

        SecretTypes secretTypes = new SecretTypes();
        List<SecretType> listOfSecretTypes = secretTypes.getSecretType();
        listOfSecretTypes.add(SecretType.E_METER_AUTHENTICATION_KEY);
        getSecretsRequest.setSecretTypes(secretTypes);
        GetSecretsResponse response = instance.getSecretsRequest(getSecretsRequest);

    }
}
