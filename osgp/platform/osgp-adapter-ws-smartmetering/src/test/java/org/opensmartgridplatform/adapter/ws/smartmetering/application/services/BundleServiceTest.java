// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActionRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessageRequest;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BundleServiceTest {

  private static final String PREFIX = "prefix";
  private static final String NAME = "name";
  private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
  private static final String ORGANISATION_IDENTIFICATION = "organisationIdentification";
  private static final PlatformFunctionGroup FUNCTION_GROEP = PlatformFunctionGroup.USER;
  private static final boolean BYPASS_RETRY = false;
  private static final int MESSAGE_PRIORITY = 1;

  @InjectMocks private BundleService bundleService;

  @Mock private DomainHelperService domainHelperService;

  @Mock private CorrelationIdProviderService correlationIdProviderService;

  @Mock private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

  private Organisation organisation;
  private Device device;
  private List<ActionRequest> actionRequestMockList;

  private List<ActionRequest> createActionRequestMockList() {

    final ActionRequest a = mock(ActionRequest.class);
    final ActionRequest b = mock(ActionRequest.class);
    final ActionRequest c = mock(ActionRequest.class);
    final ActionRequest d = mock(ActionRequest.class);
    final ActionRequest e = mock(ActionRequest.class);
    final ActionRequest f = mock(ActionRequest.class);
    // when(a.getDeviceFunction()).thenReturn(DeviceFunction.REQUEST_PERIODIC_METER_DATA);
    // when(b.getDeviceFunction()).thenReturn(DeviceFunction.REQUEST_ACTUAL_METER_DATA);
    // when(c.getDeviceFunction()).thenReturn(DeviceFunction.READ_ALARM_REGISTER);
    // when(d.getDeviceFunction()).thenReturn(DeviceFunction.GET_FIRMWARE_VERSION);
    // when(e.getDeviceFunction()).thenReturn(DeviceFunction.GET_SPECIFIC_ATTRIBUTE_VALUE);
    // when(f.getDeviceFunction()).thenReturn(DeviceFunction.COUPLE_MBUS_DEVICE);

    return Arrays.asList(a, b, c, d, e, f);
  }

  @BeforeEach
  public void prepareTest() throws FunctionalException {
    this.organisation = new Organisation(ORGANISATION_IDENTIFICATION, NAME, PREFIX, FUNCTION_GROEP);
    this.device = new Device(DEVICE_IDENTIFICATION);
    this.actionRequestMockList = this.createActionRequestMockList();
    when(this.domainHelperService.findOrganisation(ORGANISATION_IDENTIFICATION))
        .thenReturn(this.organisation);
    when(this.domainHelperService.findActiveDevice(DEVICE_IDENTIFICATION)).thenReturn(this.device);
  }

  /**
   * tests that a {@link SmartMeteringRequestMessage} is send containing all the {@link
   * ActionRequest}s put in.
   *
   * @throws FunctionalException should not be thrown in this test
   */
  @Test
  void testAllOperationsAreAllowed() throws FunctionalException {
    // Run the test
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder()
            .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
            .withDeviceIdentification(DEVICE_IDENTIFICATION)
            .withMessageType(MessageType.HANDLE_BUNDLED_ACTIONS.name())
            .withMessagePriority(MESSAGE_PRIORITY)
            .withBypassRetry(BYPASS_RETRY)
            .build();

    this.bundleService.enqueueBundleRequest(messageMetadata, this.actionRequestMockList);

    // Verify the test
    final ArgumentCaptor<SmartMeteringRequestMessage> message =
        ArgumentCaptor.forClass(SmartMeteringRequestMessage.class);

    verify(this.smartMeteringRequestMessageSender).send(message.capture());

    assertThat(message.getValue().getOrganisationIdentification())
        .isEqualTo(ORGANISATION_IDENTIFICATION);
    assertThat(message.getValue().getDeviceIdentification()).isEqualTo(DEVICE_IDENTIFICATION);

    final BundleMessageRequest requestMessage =
        (BundleMessageRequest) message.getValue().getRequest();
    final List<ActionRequest> actionList = requestMessage.getBundleList();
    assertThat(actionList.size()).isEqualTo(this.actionRequestMockList.size());

    for (int i = 0; i < actionList.size(); i++) {
      assertThat(actionList.get(i)).isEqualTo(this.actionRequestMockList.get(i));
    }
  }

  /**
   * tests that a {@link FunctionalException} is thrown when the caller is not allowed to execute a
   * bundle (DeviceFunction.HANDLE_BUNDLED_ACTIONS)
   *
   * @throws FunctionalException should not be thrown in this test
   */
  @Test
  void testExceptionWhenBundleIsNotAllowed() throws FunctionalException {

    final FunctionalException fe =
        new FunctionalException(
            FunctionalExceptionType.UNAUTHORIZED, ComponentType.WS_SMART_METERING);

    doThrow(fe)
        .when(this.domainHelperService)
        .checkAllowed(this.organisation, this.device, DeviceFunction.HANDLE_BUNDLED_ACTIONS);

    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder()
            .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
            .withDeviceIdentification(DEVICE_IDENTIFICATION)
            .withMessageType(MessageType.HANDLE_BUNDLED_ACTIONS.name())
            .withMessagePriority(MESSAGE_PRIORITY)
            .withBypassRetry(BYPASS_RETRY)
            .build();

    // Run the test
    try {
      this.bundleService.enqueueBundleRequest(messageMetadata, this.actionRequestMockList);
    } catch (final FunctionalException e) {
      // Verify the test
      assertThat(e).isEqualTo(fe);
    }
  }

  /**
   * tests that a {@link FunctionalException} is thrown when the caller is not allowed to execute
   * DeviceFunction.REQUEST_PERIODIC_METER_DATA {@link ActionRequest} in the bundle
   *
   * @throws FunctionalException should not be thrown in this test
   */
  // @Test
  public void testExceptionWhenOperationNotAllowed() throws FunctionalException {

    // Prepare test
    final FunctionalException fe =
        new FunctionalException(
            FunctionalExceptionType.UNAUTHORIZED, ComponentType.WS_SMART_METERING);
    doThrow(fe)
        .when(this.domainHelperService)
        .checkAllowed(this.organisation, this.device, DeviceFunction.REQUEST_PERIODIC_METER_DATA);

    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder()
            .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
            .withDeviceIdentification(DEVICE_IDENTIFICATION)
            .withMessageType(MessageType.HANDLE_BUNDLED_ACTIONS.name())
            .withMessagePriority(MESSAGE_PRIORITY)
            .withBypassRetry(BYPASS_RETRY)
            .build();

    // Run the test
    try {
      this.bundleService.enqueueBundleRequest(messageMetadata, this.actionRequestMockList);
      fail();
    } catch (final FunctionalException e) {
      // Verify the test
      assertThat(e).isEqualTo(fe);
    }
  }
}
