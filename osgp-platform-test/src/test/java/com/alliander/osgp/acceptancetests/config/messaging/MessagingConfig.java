package com.alliander.osgp.acceptancetests.config.messaging;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//@Configuration
@Import({ WsAdminMessagingConfig.class, WsCoreMessagingConfig.class, WsPublicLightingMessagingConfig.class,
        WsTariffSwitchingMessagingConfig.class, DomainAdminMessagingConfig.class, DomainCoreMessagingConfig.class,
        DomainPublicLightingMessagingConfig.class, DomainTariffSwitchingMessagingConfig.class,
        OsgpCoreMessagingConfig.class, ProtocolOslpMessagingConfig.class })
//@Import({ WsPublicLightingMessagingConfig.class, DomainPublicLightingMessagingConfig.class,
//        OsgpCoreMessagingConfig.class, ProtocolOslpMessagingConfig.class })
public class MessagingConfig {

    // ========================================================================
    // CONSTANTS
    // ========================================================================

    // QUEUES BETWEEN WS ADAPTERS & DOMAIN ADAPTERS
    public static final String DOMAIN_ADMIN_1_0__WS_ADMIN_1_0__REQUESTS_QUEUE = "osgp-test.domain-admin.1_0.ws-admin.1_0.requests";
    public static final String WS_ADMIN_1_0__DOMAIN_ADMIN_1_0__RESPONSES_QUEUE = "osgp-test.ws-admin.1_0.domain-admin.1_0.responses";

    public static final String DOMAIN_CORE_1_0__WS_CORE_1_0__REQUESTS_QUEUE = "osgp-test.domain-core.1_0.ws-core.1_0.requests";
    public static final String WS_CORE_1_0__DOMAIN_CORE_1_0__RESPONSES_QUEUE = "osgp-test.ws-core.1_0.domain-core.1_0.responses";

    public static final String DOMAIN_PUBLICLIGHTING_1_0__WS_PUBLICLIGHTING_1_0__REQUESTS_QUEUE = "osgp-test.domain-publiclighting.1_0.ws-publiclighting.1_0.requests";
    public static final String WS_PUBLICLIGHTING_1_0__DOMAIN_PUBLICLIGHTING_1_0__RESPONSES_QUEUE = "osgp-test.ws-publiclighting.1_0.domain-publiclighting.1_0.responses";

    public static final String DOMAIN_TARIFFSWITCHING_1_0__WS_TARIFFSWITCHING_1_0__REQUESTS_QUEUE = "osgp-test.domain-tariffswitching.1_0.ws-tariffswitching.1_0.requests";
    public static final String WS_TARIFFSWITCHING_1_0__DOMAIN_TARIFFSWITCHING_1_0__RESPONSES_QUEUE = "osgp-test.ws-tariffswitching.1_0.domain-tariffswitching.1_0.responses";

    // QUEUES BETWEEN DOMAIN ADAPTERS & OSGP CORE
    public static final String OSGP_CORE_1_0__DOMAIN_ADMIN_1_0__REQUESTS_QUEUE = "osgp-test.osgp-core.1_0.domain-admin.1_0.requests";
    public static final String DOMAIN_ADMIN_1_0__OSGP_CORE_1_0__RESPONSES_QUEUE = "osgp-test.domain-admin.1_0.osgp-core.1_0.responses";

    public static final String DOMAIN_ADMIN_1_0__OSGP_CORE_1_0__REQUESTS_QUEUE = "osgp-test.domain-admin.1_0.osgp-core.1_0.requests";
    public static final String OSGP_CORE_1_0__DOMAIN_ADMIN_1_0__RESPONSES_QUEUE = "osgp-test.osgp-core.1_0.domain-admin.1_0.responses";

    public static final String OSGP_CORE_1_0__DOMAIN_CORE_1_0__REQUESTS_QUEUE = "osgp-test.osgp-core.1_0.domain-core.1_0.requests";
    public static final String DOMAIN_CORE_1_0__OSGP_CORE_1_0__RESPONSES_QUEUE = "osgp-test.domain-core.1_0.osgp-core.1_0.responses";

    public static final String DOMAIN_CORE_1_0__OSGP_CORE_1_0__REQUESTS_QUEUE = "osgp-test.domain-core.1_0.osgp-core.1_0.requests";
    public static final String OSGP_CORE_1_0__DOMAIN_CORE_1_0__RESPONSES_QUEUE = "osgp-test.osgp-core.1_0.domain-core.1_0.responses";

    public static final String OSGP_CORE_1_0__DOMAIN_PUBLICLIGHTING_1_0__REQUESTS_QUEUE = "osgp-test.osgp-core.1_0.domain-publiclighting.1_0.requests";
    public static final String DOMAIN_PUBLICLIGHTING_1_0__OSGP_CORE_1_0__RESPONSES_QUEUE = "osgp-test.domain-publiclighting.1_0.osgp-core.1_0.responses";

    public static final String DOMAIN_PUBLICLIGHTING_1_0__OSGP_CORE_1_0__REQUESTS_QUEUE = "osgp-test.domain-publiclighting.1_0.osgp-core.1_0.requests";
    public static final String OSGP_CORE_1_0__DOMAIN_PUBLICLIGHTING_1_0__RESPONSES_QUEUE = "osgp-test.osgp-core.1_0.domain-publiclighting.1_0.responses";

    public static final String OSGP_CORE_1_0__DOMAIN_TARIFFSWITCHING_1_0__REQUESTS_QUEUE = "osgp-test.osgp-core.1_0.domain-tariffswitching.1_0.requests";
    public static final String DOMAIN_TARIFFSWITCHING_1_0__OSGP_CORE_1_0__RESPONSES_QUEUE = "osgp-test.domain-tariffswitching.1_0.osgp-core.1_0.responses";

    public static final String DOMAIN_TARIFFSWITCHING_1_0__OSGP_CORE_1_0__REQUESTS_QUEUE = "osgp-test.domain-tariffswitching.1_0.osgp-core.1_0.requests";
    public static final String OSGP_CORE_1_0__DOMAIN_TARIFFSWITCHING_1_0__RESPONSES_QUEUE = "osgp-test.osgp-core.1_0.domain-tariffswitching.1_0.responses";

    // QUEUES BETWEEN OSGP CORE & PROTOCOL ADAPTERS
    public static final String PROTOCOL_OSLP_1_0__OSGP_CORE_1_0__REQUESTS_QUEUE = "osgp-test.protocol-oslp.1_0.osgp-core.1_0.requests";
    public static final String OSGP_CORE_1_0__PROTOCOL_OSLP_1_0__RESPONSES_QUEUE = "osgp-test.osgp-core.1_0.protocol-oslp.1_0.responses";

    public static final String OSGP_CORE_1_0__PROTOCOL_OSLP_1_0__REQUESTS_QUEUE = "osgp-test.osgp-core.1_0.protocol-oslp.1_0.requests";
    public static final String PROTOCOL_OSLP_1_0__OSGP_CORE_1_0__RESPONSES_QUEUE = "osgp-test.protocol-oslp.1_0.osgp-core.1_0.responses";

    // LOGGING QUEUES
    public static final String WS_LOGGING_QUEUE = "osgp-test.logging.ws.1_0";
    public static final String OSLP_LOG_ITEM_REQUESTS_QUEUE = "osgp-test.logging.protocol.1_0";

    // GENERAL
    public static final String BROKER_URL = "vm://localhost";
    //    public static final String BROKER_URL = "tcp://localhost:61616";
    public static final long INITIAL_REDELIVERY_DELAY = 0;
    public static final int MAXIMUM_REDELIVERIES = 1;
    public static final long MAXIMUM_REDELIVERY_DELAY = 10;
    public static final long REDELIVERY_DELAY = 10;
    public static final double BACK_OFF_MULTIPLIER = 1;
    public static final boolean USE_EXPONENTIAL_BACK_OFF = false;
    public static final boolean EXPLICIT_QOS_ENABLED = true;
    public static final long TIME_TO_LIVE = 60000;
    public static final boolean DELIVERY_PERSISTENT = true;
    public static final long RECEIVE_TIMEOUT = 30000;
    public static final int CONCURRENT_CONSUMERS = 1;
    public static final int MAX_CONCURRENT_CONSUMERS = 1;

    // ========================================================================
    // JMS SETTINGS: GENERAL
    // ========================================================================

    @Bean(destroyMethod = "stop")
    public static PooledConnectionFactory pooledConnectionFactory() {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(connectionFactory());
        return pooledConnectionFactory;
    }

    @Bean
    public static ActiveMQConnectionFactory connectionFactory() {
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(redeliveryPolicyMap());
        activeMQConnectionFactory.setBrokerURL(BROKER_URL);

        activeMQConnectionFactory.setNonBlockingRedelivery(true);

        return activeMQConnectionFactory;
    }

    @Bean
    public static RedeliveryPolicyMap redeliveryPolicyMap() {
        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(defaultRedeliveryPolicy());
        return redeliveryPolicyMap;
    }

    @Bean
    public static RedeliveryPolicy defaultRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(INITIAL_REDELIVERY_DELAY);
        redeliveryPolicy.setMaximumRedeliveries(MAXIMUM_REDELIVERIES);
        redeliveryPolicy.setMaximumRedeliveryDelay(MAXIMUM_REDELIVERY_DELAY);
        redeliveryPolicy.setRedeliveryDelay(REDELIVERY_DELAY);

        return redeliveryPolicy;
    }
}
