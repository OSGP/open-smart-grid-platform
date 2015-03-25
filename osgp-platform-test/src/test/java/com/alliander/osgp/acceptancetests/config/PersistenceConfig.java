package com.alliander.osgp.acceptancetests.config;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;

import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.ws.infra.specifications.JpaDeviceSpecifications;
import com.alliander.osgp.adapter.ws.infra.specifications.JpaEventSpecifications;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceAuthorizationRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import com.alliander.osgp.core.db.api.repositories.DeviceDataRepository;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.DomainInfoRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.OslpLogItemRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.domain.core.specifications.DeviceSpecifications;
import com.alliander.osgp.domain.core.specifications.EventSpecifications;

//@Configuration
public class PersistenceConfig {

    // // WS LOGGING
    // @Bean
    // public WebServiceMonitorLogRepository WebServiceLoggingRepository() {
    // return mock(WebServiceMonitorLogRepository.class);
    // }

    @Bean
    public WritableDeviceRepository writableDeviceRepositoryMock() {
        return mock(WritableDeviceRepository.class);
    }

    @Bean
    public WritableDeviceAuthorizationRepository writableDeviceAuthorizationRepositoryMock() {
        return mock(WritableDeviceAuthorizationRepository.class);
    }

    // OSGP CORE
    @Bean
    public DeviceAuthorizationRepository deviceAuthorizationRepositoryMock() {
        return mock(DeviceAuthorizationRepository.class);
    }

    @Bean
    public DeviceRepository deviceRepositoryMock() {
        return mock(DeviceRepository.class);
    }

    @Bean
    public DeviceDataRepository deviceDataRepositoryMock() {
        return mock(DeviceDataRepository.class);
    }

    @Bean
    public DeviceSpecifications deviceSpecifications() {
        return new JpaDeviceSpecifications();
    }

    @Bean
    public DomainInfoRepository domainInfoRepositoryMock() {
        return mock(DomainInfoRepository.class);
    }

    @Bean
    public EventRepository eventRepositoryMock() {
        return mock(EventRepository.class);
    }

    @Bean
    public EventSpecifications eventSpecifications() {
        return new JpaEventSpecifications();
    }

    @Bean
    public OslpLogItemRepository oslpLogItemRepositoryMock() {
        return mock(OslpLogItemRepository.class);
    }

    @Bean
    public OrganisationRepository organisationRepositoryMock() {
        return mock(OrganisationRepository.class);
    }

    @Bean
    public ProtocolInfoRepository protocolInfoRepositoryMock() {
        return mock(ProtocolInfoRepository.class);
    }

    @Bean
    public ScheduledTaskRepository scheduleTaskRepositoryMock() {
        return mock(ScheduledTaskRepository.class);
    }

    // OSLP

    @Bean
    OslpDeviceRepository oslpDeviceRepositoryMock() {
        return mock(OslpDeviceRepository.class);
    }

}
