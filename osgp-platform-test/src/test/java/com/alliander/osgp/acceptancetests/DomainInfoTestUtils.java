/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.domain.core.entities.DomainInfo;

public class DomainInfoTestUtils {

    // DOMAIN INFOS
    protected static final String ADMIN_1_0_DOMAIN = "ADMIN";
    protected static final String ADMIN_1_0_DOMAIN_VERSION = "1.0";
    protected static final String ADMIN_1_0_INCOMING_DOMAIN_REQUESTS_QUEUE = "osgp-test.osgp-core.1_0.domain-admin.1_0.requests";
    protected static final String ADMIN_1_0_OUTGOING_DOMAIN_RESPONSES_QUEUE = "osgp-test.domain-admin.1_0.osgp-core.1_0.responses";
    protected static final String ADMIN_1_0_OUTGOING_DOMAIN_REQUESTS_QUEUE = "osgp-test.domain-admin.1_0.osgp-core.1_0.requests";
    protected static final String ADMIN_1_0_INCOMING_DOMAIN_RESPONSES_QUEUE = "osgp-test.osgp-core.1_0.domain-admin.1_0.responses";

    protected static final String CORE_1_0_DOMAIN = "CORE";
    protected static final String CORE_1_0_DOMAIN_VERSION = "1.0";
    protected static final String CORE_1_0_INCOMING_DOMAIN_REQUESTS_QUEUE = "osgp-test.osgp-core.1_0.domain-core.1_0.requests";
    protected static final String CORE_1_0_OUTGOING_DOMAIN_RESPONSES_QUEUE = "osgp-test.domain-core.1_0.osgp-core.1_0.responses";
    protected static final String CORE_1_0_OUTGOING_DOMAIN_REQUESTS_QUEUE = "osgp-test.domain-core.1_0.osgp-core.1_0.requests";
    protected static final String CORE_1_0_INCOMING_DOMAIN_RESPONSES_QUEUE = "osgp-test.osgp-core.1_0.domain-core.1_0.responses";

    protected static final String PUBLIC_LIGHTING_1_0_DOMAIN = "PUBLIC_LIGHTING";
    protected static final String PUBLIC_LIGHTING_1_0_DOMAIN_VERSION = "1.0";
    protected static final String PUBLIC_LIGHTING_1_0_INCOMING_DOMAIN_REQUESTS_QUEUE = "osgp-test.osgp-core.1_0.domain-publiclighting.1_0.requests";
    protected static final String PUBLIC_LIGHTING_1_0_OUTGOING_DOMAIN_RESPONSES_QUEUE = "osgp-test.domain-publiclighting.1_0.osgp-core.1_0.responses";
    protected static final String PUBLIC_LIGHTING_1_0_OUTGOING_DOMAIN_REQUESTS_QUEUE = "osgp-test.domain-publiclighting.1_0.osgp-core.1_0.requests";
    protected static final String PUBLIC_LIGHTING_1_0_INCOMING_DOMAIN_RESPONSES_QUEUE = "osgp-test.osgp-core.1_0.domain-publiclighting.1_0.responses";

    protected static final String TARIFF_SWITCHING_1_0_DOMAIN = "TARIFF_SWITCHING";
    protected static final String TARIFF_SWITCHING_1_0_DOMAIN_VERSION = "1.0";
    protected static final String TARIFF_SWITCHING_1_0_INCOMING_DOMAIN_REQUESTS_QUEUE = "osgp-test.osgp-core.1_0.domain-tariffswitching.1_0.requests";
    protected static final String TARIFF_SWITCHING_1_0_OUTGOING_DOMAIN_RESPONSES_QUEUE = "osgp-test.domain-tariffswitching.1_0.osgp-core.1_0.responses";
    protected static final String TARIFF_SWITCHING_1_0_OUTGOING_DOMAIN_REQUESTS_QUEUE = "osgp-test.domain-tariffswitching.1_0.osgp-core.1_0.requests";
    protected static final String TARIFF_SWITCHING_1_0_INCOMING_DOMAIN_RESPONSES_QUEUE = "osgp-test.osgp-core.1_0.domain-tariffswitching.1_0.responses";

    private static Map<String, DomainInfo> domainInfos = initDomainInfos();

    public static List<DomainInfo> getDomainInfos() {
        return new ArrayList<DomainInfo>(domainInfos.values());
    }

    public static DomainInfo getDomainInfo(final String domain, final String domainVersion) {
        return domainInfos.get(DomainInfo.getKey(domain, domainVersion));
    }

    private static Map<String, DomainInfo> initDomainInfos() {
        final Map<String, DomainInfo> domainInfos = new HashMap<>();
        DomainInfo domainInfo = new DomainInfo(ADMIN_1_0_DOMAIN, ADMIN_1_0_DOMAIN_VERSION,
                ADMIN_1_0_INCOMING_DOMAIN_REQUESTS_QUEUE, ADMIN_1_0_OUTGOING_DOMAIN_RESPONSES_QUEUE,
                ADMIN_1_0_OUTGOING_DOMAIN_REQUESTS_QUEUE, ADMIN_1_0_INCOMING_DOMAIN_RESPONSES_QUEUE);
        domainInfos.put(domainInfo.getKey(), domainInfo);

        domainInfo = new DomainInfo(CORE_1_0_DOMAIN, CORE_1_0_DOMAIN_VERSION, CORE_1_0_INCOMING_DOMAIN_REQUESTS_QUEUE,
                CORE_1_0_OUTGOING_DOMAIN_RESPONSES_QUEUE, CORE_1_0_OUTGOING_DOMAIN_REQUESTS_QUEUE,
                CORE_1_0_INCOMING_DOMAIN_RESPONSES_QUEUE);
        domainInfos.put(domainInfo.getKey(), domainInfo);

        domainInfo = new DomainInfo(PUBLIC_LIGHTING_1_0_DOMAIN, PUBLIC_LIGHTING_1_0_DOMAIN_VERSION,
                PUBLIC_LIGHTING_1_0_INCOMING_DOMAIN_REQUESTS_QUEUE,
                PUBLIC_LIGHTING_1_0_OUTGOING_DOMAIN_RESPONSES_QUEUE,
                PUBLIC_LIGHTING_1_0_OUTGOING_DOMAIN_REQUESTS_QUEUE, PUBLIC_LIGHTING_1_0_INCOMING_DOMAIN_RESPONSES_QUEUE);
        domainInfos.put(domainInfo.getKey(), domainInfo);

        domainInfo = new DomainInfo(TARIFF_SWITCHING_1_0_DOMAIN, TARIFF_SWITCHING_1_0_DOMAIN_VERSION,
                TARIFF_SWITCHING_1_0_INCOMING_DOMAIN_REQUESTS_QUEUE,
                TARIFF_SWITCHING_1_0_OUTGOING_DOMAIN_RESPONSES_QUEUE,
                TARIFF_SWITCHING_1_0_OUTGOING_DOMAIN_REQUESTS_QUEUE,
                TARIFF_SWITCHING_1_0_INCOMING_DOMAIN_RESPONSES_QUEUE);
        domainInfos.put(domainInfo.getKey(), domainInfo);

        return domainInfos;
    }
}
