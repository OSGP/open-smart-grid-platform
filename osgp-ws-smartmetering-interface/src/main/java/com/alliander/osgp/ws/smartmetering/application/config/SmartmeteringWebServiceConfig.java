/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.ws.smartmetering.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;

@Configuration
public class SmartmeteringWebServiceConfig {

    // <bean id="payloadValidatingInterceptor"
    // class="org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor">
    // <property name="schemas">
    // <list>
    // <value>/WEB-INF/wsdl/smartmetering/schemas/*.xsd</value>
    // </list>
    // </property>
    // <property name="validateRequest" value="true" />
    // <property name="validateResponse" value="false" />
    // </bean>

    @Bean
    public PayloadValidatingInterceptor payloadValidatingInterceptor() {
        final PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
        payloadValidatingInterceptor.setSchema(new ClassPathResource("common.xsd"));
        return payloadValidatingInterceptor;
    }

}
