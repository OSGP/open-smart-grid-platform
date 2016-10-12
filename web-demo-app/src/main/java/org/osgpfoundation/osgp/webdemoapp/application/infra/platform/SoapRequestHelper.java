package org.osgpfoundation.osgp.webdemoapp.application.infra.platform;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

public class SoapRequestHelper {
    private Jaxb2Marshaller marshaller;
    private KeyStoreHelper keyStoreHelper;

    private SaajSoapMessageFactory messageFactory;

    public SoapRequestHelper(final SaajSoapMessageFactory messageFactory, final KeyStoreHelper keyStoreHelper) {
        this.messageFactory = messageFactory;
        this.keyStoreHelper = keyStoreHelper;
    }

    /**
     * Helper function to create a web service template to handle soap requests
     * for the Admin domain
     *
     * @return WebServiceTemplate
     */
    public WebServiceTemplate createAdminRequest() {
        this.initMarshaller("com.alliander.osgp.platform.ws.schema.admin.devicemanagement");

        final String uri = "https://localhost/osgp-adapter-ws-admin/admin/deviceManagementService/DeviceManagement";

        final WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

        webServiceTemplate.setDefaultUri(uri);
        webServiceTemplate.setMarshaller(this.marshaller);
        webServiceTemplate.setUnmarshaller(this.marshaller);

        webServiceTemplate.setCheckConnectionForFault(true);

        webServiceTemplate.setInterceptors(new ClientInterceptor[] {
                this.createClientInterceptor("http://www.alliander.com/schemas/osp/common") });

        webServiceTemplate.setMessageSender(this.createHttpMessageSender());

        return webServiceTemplate;
    }

    /**
     * Helper function to create a web service template to handle soap requests
     * for the Public Lighting domain
     *
     * @return WebServiceTemplate
     */
    public WebServiceTemplate createPublicLightingRequest() {
        this.initMarshaller("com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement");

        final String uri = "https://localhost/osgp-adapter-ws-publiclighting/publiclighting/adHocManagementService/AdHocManagement";

        final WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

        webServiceTemplate.setDefaultUri(uri);
        webServiceTemplate.setMarshaller(this.marshaller);
        webServiceTemplate.setUnmarshaller(this.marshaller);

        webServiceTemplate.setCheckConnectionForFault(true);

        webServiceTemplate.setInterceptors(new ClientInterceptor[] {
                this.createClientInterceptor("http://www.alliander.com/schemas/osgp/common") });

        webServiceTemplate.setMessageSender(this.createHttpMessageSender());

        return webServiceTemplate;
    }

    /**
     * Initializes the JaxB Marshaller
     *
     * @param marshallerContext
     */
    private void initMarshaller(final String marshallerContext) {
        this.marshaller = new Jaxb2Marshaller();

        this.marshaller.setContextPath(marshallerContext);
    }

    /**
     * Creates a HttpComponentsMessageSender for communication with the
     * platform.
     *
     * @return HttpComponentsMessageSender
     */
    private HttpComponentsMessageSender createHttpMessageSender() {

        final HttpComponentsMessageSender sender = new HttpComponentsMessageSender();
        final HttpClient client = sender.getHttpClient();
        SSLSocketFactory socketFactory;
        try {
            socketFactory = new SSLSocketFactory(this.keyStoreHelper.getKeyStore(), this.keyStoreHelper.getKeyStorePw(),
                    this.keyStoreHelper.getTrustStore());

            final Scheme scheme = new Scheme("https", 443, socketFactory);

            client.getConnectionManager().getSchemeRegistry().register(scheme);

        } catch (KeyManagementException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }

        return sender;
    }

    /**
     * Create a ClientIntercepter, used for the WebServiceTemplate.
     *
     * @param namespace
     * @return ClientInterceptor
     */
    private ClientInterceptor createClientInterceptor(final String namespace) {
        return new IdentificationClientInterceptor("test-org", "demo-app-user", "demo-app", namespace,
                "OrganisationIdentification", "UserName", "ApplicationName");
    }

}
