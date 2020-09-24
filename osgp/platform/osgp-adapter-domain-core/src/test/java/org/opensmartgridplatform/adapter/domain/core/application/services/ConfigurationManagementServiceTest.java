package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

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
import org.opensmartgridplatform.adapter.domain.core.application.mapping.DomainCoreMapper;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.Configuration;
import org.opensmartgridplatform.domain.core.valueobjects.RelayConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.RelayMap;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Qualifier;

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
    private DomainCoreMapper domainCoreMapper;
    @Mock
    private OrganisationDomainService organisationDomainService;
    @Mock
    private DeviceDomainService deviceDomainService;
    @Mock
    private CorrelationIds ids;
    @Mock
    private Configuration configuration;
    @Mock
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;
    @Mock
    private Ssld ssld;
    @Mock
    private Device device;
    @Captor
    private ArgumentCaptor<RequestMessage> requestMessageArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptorOne;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptorTwo;
    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;
    @Captor
    private ArgumentCaptor<ResponseMessage> responseMessageArgumentCaptor;
    @InjectMocks
    @Qualifier("organisationDomainService")
    private ConfigurationManagementService configurationManagementService;
    private final String organisationIdentification = "a";
    private final String deviceIdentification = "b";
    private final String correlationUid = "c";
    private OsgpException exception;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    @BeforeEach
    public void setUp() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        // make ids
        this.ids = new CorrelationIds(this.organisationIdentification, this.deviceIdentification, this.correlationUid);
        this.configurationManagementService = new ConfigurationManagementService();

        // do injection using reflection
        // Inject Mocks doesn't inject these mocks, possibly because they are in the parent class of
        // ConfigurationManagementService
        this.injectionUsingReflection(AbstractService.class, "organisationDomainService",
                this.configurationManagementService, this.organisationDomainService);
        this.injectionUsingReflection(AbstractService.class, "deviceDomainService", this.configurationManagementService,
                this.deviceDomainService);
        this.injectionUsingReflection(AbstractService.class, "domainCoreMapper", this.configurationManagementService,
                this.domainCoreMapper);
        this.injectionUsingReflection(AbstractService.class, "osgpCoreRequestMessageSender",
                this.configurationManagementService, this.osgpCoreRequestMessageSender);
        this.injectionUsingReflection(AbstractService.class, "webServiceResponseMessageSender",
                this.configurationManagementService, this.webServiceResponseMessageSender);
        this.injectionUsingReflection(AbstractService.class, "ssldRepository", this.configurationManagementService,
                this.ssldRepository);

        System.setOut(new PrintStream(this.outContent));
    }
    @Test
    public void testTrySetConfiguration() throws FunctionalException, UnknownEntityException {
        final long scheduleTime = 1;
        final String messageType = "none";
        final int messagePriority = 1;

        final Organisation testOrganisation = new Organisation();

        final RelayMap relayMap = new RelayMap(1, 1, RelayType.LIGHT, "1");
        final ArrayList<RelayMap> relayMapList = new ArrayList<>();
        relayMapList.add(relayMap);

        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(testOrganisation);
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(this.device);
        when(this.configuration.getRelayConfiguration()).thenReturn(new RelayConfiguration(relayMapList));
        when(this.ssldRepository.findById(any())).thenReturn(java.util.Optional.of(this.ssld));
        when(this.domainCoreMapper.map(any(), any())).thenReturn(this.configurationDto);
        doNothing().when(this.ssld).updateOutputSettings(any());
        when(this.device.getIpAddress()).thenReturn("333.333.1.22");

        this.configurationManagementService.setConfiguration(this.ids, this.configuration, scheduleTime,
                messageType, messagePriority);

        verify(this.osgpCoreRequestMessageSender).sendWithScheduledTime(this.requestMessageArgumentCaptor.capture(),
                this.stringArgumentCaptorOne.capture(), this.integerArgumentCaptor.capture(),
                this.stringArgumentCaptorTwo.capture(), this.longArgumentCaptor.capture());

        assertThat(this.requestMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("c");
        assertThat(this.requestMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("a");
        assertThat(this.requestMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("b");
        assertThat(this.stringArgumentCaptorOne.getValue()).isEqualTo(messageType);
        assertThat(this.integerArgumentCaptor.getValue()).isEqualTo(1);
        assertThat(this.stringArgumentCaptorTwo.getValue()).isEqualTo("333.333.1.22");
        assertThat(this.longArgumentCaptor.getValue()).isEqualTo(1);
    }
    @Test
    public void testTrySetConfigurationWithNoConfiguration() throws Exception {
        final long scheduleTime = 1;
        final String messageType = "none";
        final int messagePriority = 1;

        final Organisation testOrganisation = new Organisation();
        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(testOrganisation);
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(new Device());
        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(testOrganisation);

        this.configurationManagementService.setConfiguration(this.ids, null, scheduleTime, messageType, messagePriority);

        assertThat(this.outContent.toString().contains("Configuration is empty, skip sending a request to device")).isTrue();
    }
    @Test
    public void
    testTrySetConfigurationWithNullRelayConfiguration() throws UnknownEntityException, FunctionalException {
        final long scheduleTime = 1;
        final String messageType = "none";
        final int messagePriority = 1;

        final Organisation testOrganisation = new Organisation();
        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(testOrganisation);
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(new Device());
        when(this.configuration.getRelayConfiguration()).thenReturn(null);

        this.configurationManagementService.setConfiguration(this.ids, this.configuration, scheduleTime,
                messageType, messagePriority);

        verify(this.osgpCoreRequestMessageSender).sendWithScheduledTime(this.requestMessageArgumentCaptor.capture(),
                this.stringArgumentCaptorOne.capture(), this.integerArgumentCaptor.capture(),
                this.stringArgumentCaptorTwo.capture(), this.longArgumentCaptor.capture());

        assertThat(this.requestMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("c");
        assertThat(this.requestMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("a");
        assertThat(this.requestMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("b");
        assertThat(this.stringArgumentCaptorOne.getValue()).isEqualTo(messageType);
        assertThat(this.integerArgumentCaptor.getValue()).isEqualTo(1);
        assertThat(this.stringArgumentCaptorTwo.getValue()).isEqualTo(null);
        assertThat(this.longArgumentCaptor.getValue()).isEqualTo(1);
    }
    @Test
    public void testGetConfiguration() throws UnknownEntityException, FunctionalException {
        final String messageType = "none";
        final int messagePriority = 1;

        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(new Organisation());
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(this.device);
        when(this.device.getIpAddress()).thenReturn("333.333.1.22");

        this.configurationManagementService.getConfiguration(this.organisationIdentification, this.deviceIdentification,
                this.correlationUid, messageType, messagePriority);

        verify(this.osgpCoreRequestMessageSender).send(this.requestMessageArgumentCaptor.capture(),
                this.stringArgumentCaptorOne.capture(), this.integerArgumentCaptor.capture(),
                this.stringArgumentCaptorTwo.capture());

        assertThat(this.requestMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("c");
        assertThat(this.requestMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("a");
        assertThat(this.requestMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("b");
        assertThat(this.stringArgumentCaptorOne.getValue()).isEqualTo(messageType);
        assertThat(this.integerArgumentCaptor.getValue()).isEqualTo(1);
        assertThat(this.stringArgumentCaptorTwo.getValue()).isEqualTo("333.333.1.22");
    }
    @Test
    public void testHandleGetConfigurationResponse() throws UnknownEntityException, FunctionalException {
        final String messageType = "none";
        final int messagePriority = 1;
        this.exception = null;
        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(new Organisation());
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(new Device());
        when(this.ssldRepository.findByDeviceIdentification("b")).thenReturn(new Ssld());

        this.configurationManagementService.handleGetConfigurationResponse(this.configurationDto, this.ids,
                messageType, messagePriority,
                ResponseMessageResultType.OK, this.exception);

        verify(this.webServiceResponseMessageSender).send(this.responseMessageArgumentCaptor.capture());

        assertThat(this.responseMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("c");
        assertThat(this.responseMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("a");
        assertThat(this.responseMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("b");
        assertThat(this.responseMessageArgumentCaptor.getValue().getResult()).isEqualTo(ResponseMessageResultType.OK);
    }
    @Test
    public void testHandleGetConfigurationResponseWithException() throws UnknownEntityException, FunctionalException {
        final String messageType = "none";
        final int messagePriority = 1;
        this.exception = new OsgpException(ComponentType.DOMAIN_ADMIN, "a");

        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(new Organisation());
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(new Device());
        when(this.ssldRepository.findByDeviceIdentification("b")).thenReturn(new Ssld());

        this.configurationManagementService.handleGetConfigurationResponse(this.configurationDto, this.ids,
                messageType, messagePriority, ResponseMessageResultType.OK, this.exception);

        assertThat(this.outContent.toString().contains("Unexpected Exception for messageType:")).isTrue();

        verify(this.webServiceResponseMessageSender).send(this.responseMessageArgumentCaptor.capture());

        assertThat(this.responseMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("c");
        assertThat(this.responseMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("a");
        assertThat(this.responseMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("b");
        assertThat(this.responseMessageArgumentCaptor.getValue().getResult()).isEqualTo(ResponseMessageResultType.NOT_OK);
    }
    @Test
    public void testswitchConfiguration() throws UnknownEntityException, FunctionalException {
        final String messageType = "none";
        final int messagePriority = 1;

        when(this.organisationDomainService.searchOrganisation(any(String.class))).thenReturn(new Organisation());
        when(this.deviceDomainService.searchActiveDevice(any(), any())).thenReturn(this.device);
        when(this.device.getIpAddress()).thenReturn("333.333.1.22");

        this.configurationManagementService.switchConfiguration(this.organisationIdentification,
                this.deviceIdentification, this.correlationUid, messageType, messagePriority, "a");

        verify(this.osgpCoreRequestMessageSender).send(this.requestMessageArgumentCaptor.capture(),
                this.stringArgumentCaptorOne.capture(), this.integerArgumentCaptor.capture(),
                this.stringArgumentCaptorTwo.capture());

        assertThat(this.requestMessageArgumentCaptor.getValue().getCorrelationUid()).isEqualTo("c");
        assertThat(this.requestMessageArgumentCaptor.getValue().getOrganisationIdentification()).isEqualTo("a");
        assertThat(this.requestMessageArgumentCaptor.getValue().getDeviceIdentification()).isEqualTo("b");
        assertThat(this.stringArgumentCaptorOne.getValue()).isEqualTo(messageType);
        assertThat(this.integerArgumentCaptor.getValue()).isEqualTo(1);
        assertThat(this.stringArgumentCaptorTwo.getValue()).isEqualTo("333.333.1.22");
    }

    private void injectionUsingReflection(final Class<?> c, final String fieldName, final Object instance, final Object newValue) throws
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final Field field = c.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, newValue);
    }
}