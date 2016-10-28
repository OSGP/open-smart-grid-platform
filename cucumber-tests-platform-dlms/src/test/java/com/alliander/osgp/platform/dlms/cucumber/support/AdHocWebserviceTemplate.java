package com.alliander.osgp.platform.dlms.cucumber.support;

import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.alliander.osgp.adapter.ws.smartmetering.infra.ws.WebServiceTemplateFactory;

public class AdHocWebserviceTemplate extends AbstractWebServiceConfig {

    @Bean
    public WebServiceTemplateFactory webServiceAdhocTemplateFactory() {
        return new WebServiceTemplateFactory(
                this.smartMeteringAdHocMarshaller(),
                this.messageFactory(),
                this.webserviceKeystoreType,
                this.webserviceKeystoreLocation,
                this.webserviceKeystorePassword,
                this.webServiceTrustStoreFactory(),
                this.applicationName);
    }

    @Bean
    public Jaxb2Marshaller smartMeteringAdHocMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.alliander.osgp.platform.ws.schema.smartmetering.adhoc"); //this.contextPathSmartmeteringBatch);
        return marshaller;
    }

}
