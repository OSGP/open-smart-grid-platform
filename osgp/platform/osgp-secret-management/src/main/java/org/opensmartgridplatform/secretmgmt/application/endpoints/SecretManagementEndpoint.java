package org.opensmartgridplatform.secretmgmt.application.endpoints;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.GetSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.OsgpResultType;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsRequest;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.StoreSecretsResponse;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TechnicalFault;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecrets;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmgmt.application.exception.TechnicalServiceFaultException;
import org.opensmartgridplatform.secretmgmt.application.services.SecretManagementService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

import static org.opensmartgridplatform.secretmgmt.application.config.ApplicationConfig.COMPONENT_NAME;

@Endpoint
@Slf4j
public class SecretManagementEndpoint {

    private static final String NAMESPACE_URI = "http://www.opensmartgridplatform.org/schemas/security/secretmanagement/2020/05";

    private final SecretManagementService secretManagementService;
    private final EndPointDataTypeConverter converter;

    public SecretManagementEndpoint(SecretManagementService secretManagementService, EndPointDataTypeConverter converter) {
        this.secretManagementService = secretManagementService;
        this.converter = converter;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSecretsRequest")
    @ResponsePayload
    public GetSecretsResponse getSecretsRequest(@RequestPayload GetSecretsRequest request) {

        log.info("Handling incoming SOAP request 'getSecretsRequest' for device {}", request.getDeviceId());

        try {
            GetSecretsResponse response = new GetSecretsResponse();

            List<SecretType> secretTypeList = converter.convertToSecretTypes(request.getSecretTypes());
            List<TypedSecret> typedSecrets = secretManagementService.retrieveSecrets(request.getDeviceId(), secretTypeList);

            TypedSecrets soapTypedSecrets = converter.convertToSoapTypedSecrets(typedSecrets);

            response.setTypedSecrets(soapTypedSecrets);
            response.setResult(OsgpResultType.OK);

            return response;
        }
        catch (Exception e) {
            throw new TechnicalServiceFaultException(e.getMessage(), e, createTechnicalFaultFromException(e));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "storeSecretsRequest")
    @ResponsePayload
    public StoreSecretsResponse storeSecretsRequest(@RequestPayload StoreSecretsRequest request) throws Exception {

        log.info("Handling incoming SOAP request 'storeSecretsRequest' for device {}", request.getDeviceId());

        StoreSecretsResponse response = new StoreSecretsResponse();

        try {
            List<TypedSecret> typedSecretList = converter.convertToTypedSecrets(request.getTypedSecrets());

            secretManagementService.storeSecrets(request.getDeviceId(), typedSecretList);

            response.setResult(OsgpResultType.OK);

            return response;
        }
        catch (Exception e) {
            throw new TechnicalServiceFaultException(e.getMessage(), e, createTechnicalFaultFromException(e));
        }
    }

    private TechnicalFault createTechnicalFaultFromException(Exception e) {
        TechnicalFault fault = new TechnicalFault();
        fault.setMessage(e.getMessage());
        fault.setComponent(COMPONENT_NAME);
        if (e.getCause() != null) {
            fault.setInnerException(e.getCause().toString());
            fault.setInnerMessage(e.getCause().getMessage());
        }
        return fault;
    }

}
