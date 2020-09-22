package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.activemq.command.ActiveMQMessage;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.dto.da.ConnectRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.*;
import org.opensmartgridplatform.adapter.domain.core.application.mapping.DomainCoreMapper;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.Configuration;
import org.opensmartgridplatform.domain.core.valueobjects.RelayConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.RelayMap;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.LoggingEvent;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConfigurationManagementServiceTest {
    @Mock
    private WebServiceResponseMessageSender webServiceResponseMessageSender;
    @Mock
    private SsldRepository ssldRepository;
    @Mock
    private ConfigurationDto configurationDto;
    @Mock
    private Organisation organisation;
    @Mock
    private OrganisationRepository organisationRepository;
    @Mock
    private DomainCoreMapper domainCoreMapper;
    @Mock
    private OrganisationDomainService organisationDomainService;
    @Mock
    private DeviceDomainService deviceDomainService;
    @Mock
    private ActiveMQMessage message;
    @Mock
    private CorrelationIds ids;
    @Mock
    private Configuration configuration;
    @Mock
    private ConfigurationDto.Builder builder;
    @Mock
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;
    @Mock
    private Ssld ssld;
    @InjectMocks
    @Qualifier("organisationDomainService")
    private ConfigurationManagementService configurationManagementService;
    private long scheduleTime;
    private String messageType;
    private int messagePriority;
    private String organisationIdentification;
    private String deviceIdentification;
    private String correlationUid;
    private ResponseMessageResultType deviceResult;
    private OsgpException exception;
    @BeforeEach
    public void setUp() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        // make ids
        ids = new CorrelationIds("a", "b", "c");
        configurationManagementService = new ConfigurationManagementService();
        // do injection using reflection
        injectionUsingReflection(AbstractService.class, "organisationDomainService", configurationManagementService, organisationDomainService);
        injectionUsingReflection(AbstractService.class, "deviceDomainService", configurationManagementService, deviceDomainService);
        injectionUsingReflection(AbstractService.class, "domainCoreMapper", configurationManagementService, domainCoreMapper);
        injectionUsingReflection(AbstractService.class, "osgpCoreRequestMessageSender", configurationManagementService, osgpCoreRequestMessageSender);
        injectionUsingReflection(AbstractService.class, "webServiceResponseMessageSender", configurationManagementService, webServiceResponseMessageSender);
        injectionUsingReflection(AbstractService.class, "ssldRepository", configurationManagementService, ssldRepository);
        scheduleTime = 1;
        messageType = "none";
        messagePriority = 1;
    }
    @Test
    public void testTrySetConfiguration() throws FunctionalException, UnknownEntityException, JMSException {
        Organisation testOrganisation = new Organisation();

        RelayMap relayMap = new RelayMap(1, 1, RelayType.LIGHT, "1");
        ArrayList<RelayMap> relayMapList = new ArrayList<>();
        relayMapList.add(relayMap);

        when(organisationDomainService.searchOrganisation(any(String.class))).thenReturn(testOrganisation);
        when(deviceDomainService.searchActiveDevice(any(), any())).thenReturn(new Device());
        when(configuration.getRelayConfiguration()).thenReturn(new RelayConfiguration(relayMapList));
        when(ssldRepository.findById(any())).thenReturn(java.util.Optional.of(ssld));
        doNothing().when(ssld).updateOutputSettings(any());
        try {
            configurationManagementService.setConfiguration(ids, configuration, scheduleTime, messageType, messagePriority);
        } catch (FunctionalException e) {
            fail("should not throw exception");
        }
        verify(ssldRepository, times(1)).save(any());
        verify(configuration, times(7)).getRelayConfiguration();
        verify(domainCoreMapper, times(1)).map(any(), any());
    }
    @Test
    public void testTrySetConfigurationWithNoConfiguration()
            throws Exception {
        Organisation testOrganisation = new
                Organisation();
        when(organisationDomainService.searchOrganisation(any(String.class))).
                thenReturn(testOrganisation);
        when(deviceDomainService.searchActiveDevice(any(),
                any())).thenReturn(new Device());
        try {
            when(organisationDomainService.searchOrganisation(any(String.class))).
                    thenReturn(testOrganisation);
        } catch (UnknownEntityException e) {
            fail("should not throw exception in mocks 1: "
                    + e);
        }
        try {
            configurationManagementService.setConfiguration(ids, null,
                    scheduleTime, messageType, messagePriority);
        } catch (FunctionalException e) {
            fail("should not throw exception in method");
        }
    }
    @Test
    public void
    testTrySetConfigurationWithNullRelayConfiguration() throws
            UnknownEntityException, FunctionalException {
        Organisation testOrganisation = new
                Organisation();
        when(organisationDomainService.searchOrganisation(any(String.class))).
                thenReturn(testOrganisation);
        when(deviceDomainService.searchActiveDevice(any(),
                any())).thenReturn(new Device());
        when(configuration.getRelayConfiguration()).thenReturn(null);
        try {
            configurationManagementService.setConfiguration(ids, configuration,
                    scheduleTime, messageType, messagePriority);
        } catch (FunctionalException e) {
            fail("should not throw exception");
        }
        verify(configuration,
                times(2)).getRelayConfiguration();
        verify(domainCoreMapper, times(1)).map(any(), any());
    }
    @Test
    public void testGetConfiguration() throws
            UnknownEntityException, FunctionalException {
        organisationIdentification = "a";
        deviceIdentification = "b";
        correlationUid = "c";
        when(organisationDomainService.searchOrganisation(any(String.class))).
                thenReturn(new Organisation());
        when(deviceDomainService.searchActiveDevice(any(),
                any())).thenReturn(new Device());
        try {
            configurationManagementService.getConfiguration(organisationIdentification,
                    deviceIdentification, correlationUid, messageType,
                    messagePriority);
        }
        catch(Exception e){
            fail("should not throw exception in method:" +
                    e);
        }
        verify(osgpCoreRequestMessageSender, times(1)).send(
                any(RequestMessage.class), eq("none"), eq(1), eq(null));
    }
    @Test
    public void testHandleGetConfigurationResponse() throws
            UnknownEntityException, FunctionalException {
        organisationIdentification = "a";
        deviceIdentification = "b";
        correlationUid = "c";
        exception = null;
        when(organisationDomainService.searchOrganisation(any(String.class))).
                thenReturn(new Organisation());
        when(deviceDomainService.searchActiveDevice(any(),
                any())).thenReturn(new Device());
        when(ssldRepository.findByDeviceIdentification("b")).thenReturn(new
                Ssld());
        try {
            configurationManagementService.handleGetConfigurationResponse(configurationDto,
                    ids, messageType, messagePriority,
                    ResponseMessageResultType.OK, exception);
        }
        catch(Exception e){
            fail("should not throw exception in method:" +
                    e);
        }
        verify(domainCoreMapper, times(1)).map(any(), any());
        verify(webServiceResponseMessageSender,
                times(1)).send(any());
    }
    @Test
    public void testHandleGetConfigurationResponseWithException()
            throws UnknownEntityException, FunctionalException {
        organisationIdentification = "a";
        deviceIdentification = "b";
        correlationUid = "c";
        exception = new
                OsgpException(ComponentType.DOMAIN_ADMIN, "a");
        when(organisationDomainService.searchOrganisation(any(String.class))).
                thenReturn(new Organisation());
        when(deviceDomainService.searchActiveDevice(any(),
                any())).thenReturn(new Device());
        when(ssldRepository.findByDeviceIdentification("b")).thenReturn(new
                Ssld());
        try {
            configurationManagementService.handleGetConfigurationResponse(configurationDto,
                    ids, messageType, messagePriority,
                    ResponseMessageResultType.OK, exception);
        }
        catch(Exception e){
            fail("should not throw exception in method:" +
                    e);
        }
        verify(domainCoreMapper, times(0)).map(any(), any());
        verify(webServiceResponseMessageSender,
                times(1)).send(any());
    }
    @Test
    public void testswitchConfiguration() throws
            UnknownEntityException, FunctionalException {
        organisationIdentification = "a";
        deviceIdentification = "b";
        correlationUid = "c";
        when(organisationDomainService.searchOrganisation(any(String.class))).
                thenReturn(new Organisation());
        when(deviceDomainService.searchActiveDevice(any(),
                any())).thenReturn(new Device());
        configurationManagementService.switchConfiguration(organisationIdentification,
                deviceIdentification, correlationUid, messageType,
                messagePriority, "a");
        try {
            configurationManagementService.switchConfiguration(organisationIdentification,
                    deviceIdentification, correlationUid, messageType,
                    messagePriority, "a");
        }
        catch(Exception e){
            fail("should not throw exception in method:" +
                    e);
        }
        verify(osgpCoreRequestMessageSender, times(2)).send(
                any(RequestMessage.class), eq("none"), eq(1), eq(null));
    }
    private void injectionUsingReflection(Class<?> c, String fieldName, Object instance, Object newValue) throws
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = c.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, newValue);
    }
}