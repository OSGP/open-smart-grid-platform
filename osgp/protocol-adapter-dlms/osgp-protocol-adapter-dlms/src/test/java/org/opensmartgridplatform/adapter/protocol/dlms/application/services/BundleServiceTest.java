/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.AbstractCommandExecutorStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.CommandExecutorMapStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionDtoBuilder;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParameterDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.OsgpResultTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class BundleServiceTest {

  @InjectMocks private BundleService bundleService;

  private final ActionDtoBuilder builder = new ActionDtoBuilder();

  @Spy private final CommandExecutorMapStub bundleCommandExecutorMap = new CommandExecutorMapStub();

  private final List<FaultResponseParameterDto> parameters = new ArrayList<>();
  private final ComponentType defaultComponent = ComponentType.PROTOCOL_DLMS;
  private final MessageMetadata messageMetadata =
      MessageMetadata.newBuilder().withCorrelationUid("123456").build();

  @Test
  public void testHappyFlow() {
    final List<ActionDto> actionDtoList = this.makeActions();
    final BundleMessagesRequestDto dto = new BundleMessagesRequestDto(actionDtoList);
    final BundleMessagesRequestDto result = this.callExecutors(dto, this.messageMetadata);
    this.assertResult(result);
  }

  @Test
  public void testException() {
    final List<ActionDto> actionDtoList = this.makeActions();
    final BundleMessagesRequestDto dto = new BundleMessagesRequestDto(actionDtoList);
    this.getStub(FindEventsRequestDto.class)
        .failWith(new ProtocolAdapterException("simulate error"));
    final BundleMessagesRequestDto result = this.callExecutors(dto, this.messageMetadata);
    this.assertResult(result);
  }

  /**
   * Tests the retry mechanism works in the adapter-protocol. In the first run a ConnectionException
   * is thrown while executing the {@link FindEventsRequestDto}. In the second attempt (when the
   * connection is restored again) the rest of the actions are executed.
   *
   * @throws ProtocolAdapterException is not thrown in this test
   */
  @Test
  public void testConnectionException() throws ProtocolAdapterException {
    final List<ActionDto> actionDtoList = this.makeActions();
    final BundleMessagesRequestDto dto = new BundleMessagesRequestDto(actionDtoList);

    // Set the point where to throw the ConnectionException
    this.getStub(FindEventsRequestDto.class)
        .failWithRuntimeException(new ConnectionException("Connection Exception thrown!"));

    try {
      // Execute all the actions
      this.callExecutors(dto, this.messageMetadata);
      fail("A ConnectionException should be thrown");
    } catch (final ConnectionException connectionException) {
      // The execution is stopped. The number of responses is equal to the
      // actions performed before the point the exception is thrown. See
      // also the order of the ArrayList in method 'makeActions'.
      assertThat(dto.getAllResponses().size()).isEqualTo(9);
    }

    // Reset the point where the exception was thrown.
    this.getStub(FindEventsRequestDto.class).failWithRuntimeException(null);

    try {
      // Execute the remaining actions
      this.callExecutors(dto, this.messageMetadata);
      assertThat(actionDtoList.size()).isEqualTo(dto.getAllResponses().size());
    } catch (final ConnectionException connectionException) {
      fail("A ConnectionException should not have been thrown.");
    }
  }

  @Test
  public void exceptionDetailsWithDefaultComponentInFaultResponse() throws Exception {

    final String message = "Unexpected null/unspecified value for M-Bus Capture Time";
    final Exception exception = new ProtocolAdapterException(message);

    this.parameters.add(new FaultResponseParameterDto("deviceIdentification", "ESIM1400000000123"));
    this.parameters.add(
        new FaultResponseParameterDto("gasDeviceIdentification", "ESIMG140000000841"));
    this.parameters.add(new FaultResponseParameterDto("channel", "3"));

    final String defaultMessage = "Unable to handle request";
    final FaultResponseDto faultResponse =
        this.bundleService.faultResponseForException(exception, this.parameters, defaultMessage);

    this.assertResponse(
        faultResponse,
        null,
        defaultMessage,
        this.defaultComponent.name(),
        exception.getClass().getName(),
        message,
        this.parameters);
  }

  @Test
  public void shouldHandleActionsWithoutPreviousResult() {
    final BundleMessagesRequestDto bundleMessagesRequest =
        new BundleMessagesRequestDto(
            Arrays.asList(
                this.createAction(this.builder.makePeriodicMeterReadsRequestDataDto(), null)));

    final BundleMessagesRequestDto result =
        this.bundleService.callExecutors(
            null, new DlmsDevice(), bundleMessagesRequest, this.messageMetadata);

    verify(this.bundleCommandExecutorMap)
        .getCommandExecutor(PeriodicMeterReadsRequestDataDto.class);
    assertThat(result.getAllResponses().size()).isOne();
    assertThat(result.getAllResponses().get(0)).isNotNull();
  }

  @Test
  public void shouldHandleActionsWithResultContainingFaultResponseWithRetryableException() {
    final ActionResponseDto faultResponse = this.createFaultResponse(true);
    final BundleMessagesRequestDto bundleMessagesRequest =
        new BundleMessagesRequestDto(
            Arrays.asList(
                this.createAction(
                    this.builder.makePeriodicMeterReadsRequestDataDto(), faultResponse)));

    final BundleMessagesRequestDto result =
        this.bundleService.callExecutors(
            null, new DlmsDevice(), bundleMessagesRequest, this.messageMetadata);

    verify(this.bundleCommandExecutorMap)
        .getCommandExecutor(PeriodicMeterReadsRequestDataDto.class);
    assertThat(result.getAllResponses().size()).isOne();
    assertThat(result.getAllResponses().get(0)).isNotNull();
  }

  @Test
  public void shouldNotHandleActionsContainingResult() {
    final ActionResponseDto previousActionResponse = new ActionResponseDto();
    final BundleMessagesRequestDto bundleMessagesRequest =
        new BundleMessagesRequestDto(
            Arrays.asList(
                this.createAction(
                    this.builder.makePeriodicMeterReadsRequestDataDto(), previousActionResponse)));

    final BundleMessagesRequestDto result =
        this.bundleService.callExecutors(
            null, new DlmsDevice(), bundleMessagesRequest, this.messageMetadata);

    verify(this.bundleCommandExecutorMap, never())
        .getCommandExecutor(PeriodicMeterReadsRequestDataDto.class);
    assertThat(result.getAllResponses().size()).isOne();
    assertThat(result.getAllResponses().get(0)).isEqualTo(previousActionResponse);
  }

  private ActionDto createAction(
      final ActionRequestDto actionRequestDto, final ActionResponseDto actionResponseDto) {
    final ActionDto action = new ActionDto(actionRequestDto);
    action.setResponse(actionResponseDto);
    return action;
  }

  private FaultResponseDto createFaultResponse(final boolean retryable) {
    return new FaultResponseDto.Builder().withRetryable(retryable).build();
  }

  public void assertResponse(
      final FaultResponseDto actualResponse,
      final Integer expectedCode,
      final String expectedMessage,
      final String expectedComponent,
      final String expectedInnerException,
      final String expectedInnerMessage,
      final List<FaultResponseParameterDto> expectedParameterList) {

    assertThat(actualResponse).withFailMessage("FaultResponse").isNotNull();

    /*
     * Fault Response should not contain the result fields for a generic
     * Action Response, and the result should always be NOT OK.
     */
    assertThat(actualResponse.getException()).withFailMessage("exception").isNull();
    assertThat(actualResponse.getResultString()).withFailMessage("resultString").isNull();
    assertThat(actualResponse.getResult())
        .withFailMessage("result")
        .isEqualTo(OsgpResultTypeDto.NOT_OK);

    assertThat(actualResponse.getCode()).withFailMessage("code").isEqualTo(expectedCode);
    assertThat(actualResponse.getMessage()).withFailMessage("message").isEqualTo(expectedMessage);
    assertThat(actualResponse.getComponent())
        .withFailMessage("component")
        .isEqualTo(expectedComponent);
    assertThat(actualResponse.getInnerException())
        .withFailMessage("innerException")
        .isEqualTo(expectedInnerException);
    assertThat(actualResponse.getInnerMessage())
        .withFailMessage("innerMessage")
        .isEqualTo(expectedInnerMessage);

    if (expectedParameterList == null || expectedParameterList.isEmpty()) {
      assertThat(actualResponse.getFaultResponseParameters())
          .withFailMessage("parameters")
          .isNull();
    } else {
      assertThat(actualResponse.getFaultResponseParameters())
          .withFailMessage("parameters")
          .isNotNull();
      final List<FaultResponseParameterDto> actualParameterList =
          actualResponse.getFaultResponseParameters().getParameterList();
      assertThat(actualParameterList).withFailMessage("parameter list").isNotNull();
      final int numberOfParameters = expectedParameterList.size();
      assertThat(actualParameterList.size())
          .withFailMessage("number of parameters")
          .isEqualTo(numberOfParameters);
      for (int i = 0; i < numberOfParameters; i++) {
        final FaultResponseParameterDto expectedParameter = expectedParameterList.get(i);
        final FaultResponseParameterDto actualParameter = actualParameterList.get(i);
        final int parameterNumber = i + 1;
        assertThat(actualParameter.getKey())
            .withFailMessage("parameter key " + parameterNumber)
            .isEqualTo(expectedParameter.getKey());
        assertThat(actualParameter.getValue())
            .withFailMessage("parameter value " + parameterNumber)
            .isEqualTo(expectedParameter.getValue());
      }
    }
  }

  private void assertResult(final BundleMessagesRequestDto result) {
    assertThat(result).isNotNull();
    assertThat(result.getActionList()).isNotNull();
    for (final ActionDto actionDto : result.getActionList()) {
      assertThat(actionDto.getRequest()).isNotNull();
      assertThat(actionDto.getResponse()).isNotNull();
    }
  }

  private BundleMessagesRequestDto callExecutors(
      final BundleMessagesRequestDto dto, final MessageMetadata messageMetadata) {
    final DlmsDevice device = new DlmsDevice();
    return this.bundleService.callExecutors(null, device, dto, messageMetadata);
  }

  // ---- private helper methods

  private AbstractCommandExecutorStub getStub(
      final Class<? extends ActionRequestDto> actionRequestDto) {
    return (AbstractCommandExecutorStub)
        this.bundleCommandExecutorMap.getCommandExecutor(actionRequestDto);
  }

  private List<ActionDto> makeActions() {
    final List<ActionDto> actions = new ArrayList<>();
    actions.add(new ActionDto(this.builder.makeActualMeterReadsDataDtoAction()));
    actions.add(new ActionDto(this.builder.makeActualPowerQualityRequestDto()));
    actions.add(new ActionDto(this.builder.makePeriodicMeterReadsGasRequestDataDto()));
    actions.add(new ActionDto(this.builder.makePeriodicMeterReadsRequestDataDto()));
    actions.add(new ActionDto(this.builder.makeSpecialDaysRequestDataDto()));
    actions.add(new ActionDto(this.builder.makeReadAlarmRegisterDataDto()));
    actions.add(new ActionDto(this.builder.makeGetAdministrativeStatusDataDto()));
    actions.add(new ActionDto(this.builder.makeAdministrativeStatusTypeDataDto()));
    actions.add(new ActionDto(this.builder.makeActivityCalendarDataDto()));
    actions.add(new ActionDto(this.builder.makeFindEventsQueryDto()));
    actions.add(new ActionDto(this.builder.makeGMeterInfoDto()));
    actions.add(new ActionDto(this.builder.makeSetAlarmNotificationsRequestDataDto()));
    actions.add(new ActionDto(this.builder.makeSetConfigurationObjectRequestDataDto()));
    actions.add(new ActionDto(this.builder.makeSetPushSetupAlarmRequestDataDto()));
    actions.add(new ActionDto(this.builder.makeSetPushSetupSmsRequestDataDto()));
    actions.add(new ActionDto(this.builder.makeSynchronizeTimeRequestDataDto()));
    actions.add(new ActionDto(this.builder.makeGetAllAttributeValuesRequestDto()));
    actions.add(new ActionDto(this.builder.makeGetFirmwareVersionRequestDataDto()));
    return actions;
  }
}
