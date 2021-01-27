package org.opensmartgridplatform.adapter.kafka.da.application.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.opensmartgridplatform.adapter.kafka.da.domain.repositories.LocationRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(basePackageClasses = { LocationRepository.class })
public class PersistenceConfig extends AbstractPersistenceConfig {

    private static final String ENTITY_PACKAGES_TO_SCAN = "org.opensmartgridplatform.adapter.kafka.da.domain.entities";

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        return super.getDataSource();
    }

    @Override
    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return super.createFlyway();
    }

    @Override
    @DependsOn("flyway")
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return super.entityManagerFactory("OSGP_ADAPTER_KAFKA_DISTRIBUTIONAUTOMATION", this.dataSource(),
                ENTITY_PACKAGES_TO_SCAN);
    }

}
