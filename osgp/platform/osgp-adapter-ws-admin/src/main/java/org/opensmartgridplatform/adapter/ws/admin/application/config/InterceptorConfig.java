package org.opensmartgridplatform.adapter.ws.admin.application.config;

import java.util.List;

import org.opensmartgridplatform.adapter.ws.endpointinterceptors.CertificateAndSoapHeaderAuthorizationEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.WebServiceMonitorInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.X509CertificateRdnAttributeValueEndpointInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;

public class InterceptorConfig extends WsConfigurerAdapter {

    @Autowired
    private X509CertificateRdnAttributeValueEndpointInterceptor x509CertificateSubjectCnEndpointInterceptor;

    @Autowired
    private SoapHeaderEndpointInterceptor organisationIdentificationInterceptor;

    @Autowired
    private SoapHeaderInterceptor messagePriorityInterceptor;

    @Autowired
    private CertificateAndSoapHeaderAuthorizationEndpointInterceptor organisationIdentificationInCertificateCnEndpointInterceptor;

    @Autowired
    private WebServiceMonitorInterceptor webServiceMonitorInterceptor;

    @Override
    public void addInterceptors(final List<EndpointInterceptor> interceptors) {
        interceptors.add(this.x509CertificateSubjectCnEndpointInterceptor);
        interceptors.add(this.organisationIdentificationInterceptor);
        interceptors.add(this.messagePriorityInterceptor);
        interceptors.add(this.organisationIdentificationInCertificateCnEndpointInterceptor);
        // interceptors.add(payloadValidatingInterceptor);
        interceptors.add(this.webServiceMonitorInterceptor);
    }
}
