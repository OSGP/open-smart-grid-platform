// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractCommandExecutor<T, R> implements CommandExecutor<T, R> {

  @Autowired private CommandExecutorMap bundleCommandExecutorMap;

  protected static final String ERROR_IN_OBJECT_CONFIG = "Error in object config";

  private final Class<? extends ActionRequestDto> bundleExecutorMapKey;

  /**
   * Constructor for CommandExecutors that do not need to be executed in the context of bundle
   * actions.
   */
  public AbstractCommandExecutor() {
    this(null);
  }

  /**
   * Constructor for CommandExecutors that need to be executed in the context of bundle actions.
   *
   * @param clazz the class of the ActionRequestDto subtype for which this CommandExecutor needs to
   *     be called.
   */
  public AbstractCommandExecutor(final Class<? extends ActionRequestDto> clazz) {
    this.bundleExecutorMapKey = clazz;
  }

  @PostConstruct
  public void init() {
    if (this.bundleExecutorMapKey != null) {
      this.bundleCommandExecutorMap.addCommandExecutor(this.bundleExecutorMapKey, this);
    }
  }

  @Override
  public ActionResponseDto executeBundleAction(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActionRequestDto actionRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    if (this.bundleExecutorMapKey == null) {
      throw new ProtocolAdapterException(
          "Execution of " + this.getClass().getName() + " is not supported in a bundle context.");
    }

    final T commandInput = this.fromBundleRequestInput(actionRequestDto);
    log.debug(
        "Translated {} from bundle to {} for call to CommandExecutor.",
        this.className(actionRequestDto),
        this.className(commandInput));
    final R executionResult = this.execute(conn, device, commandInput, messageMetadata);
    final ActionResponseDto bundleResponse = this.asBundleResponse(executionResult);
    log.debug(
        "Translated {} to {} for bundle response after call to CommandExecutor.",
        this.className(executionResult),
        this.className(bundleResponse));
    return bundleResponse;
  }

  protected void checkActionRequestType(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    if (this.bundleExecutorMapKey != null && !this.bundleExecutorMapKey.isInstance(bundleInput)) {
      throw new ProtocolAdapterException(
          "Expected bundleInput to be of type "
              + this.bundleExecutorMapKey.getName()
              + ", got: "
              + this.className(bundleInput));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public T fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    if (bundleInput == null) {
      return null;
    }
    try {
      return (T) bundleInput;
    } catch (final ClassCastException e) {
      throw new ProtocolAdapterException(
          "Translation from bundle ActionRequestDto to CommandExecutor input for "
              + this.getClass().getName()
              + " is not implemented.",
          e);
    }
  }

  @Override
  public ActionResponseDto asBundleResponse(final R executionResult)
      throws ProtocolAdapterException {
    try {
      return (ActionResponseDto) executionResult;
    } catch (final ClassCastException e) {
      throw new ProtocolAdapterException(
          "Translation from CommandExecutor result to bundle ActionResponseDto for "
              + this.getClass().getName()
              + " is not implemented.",
          e);
    }
  }

  protected final String className(final Object object) {
    if (object == null) {
      return "null";
    }
    return object.getClass().getName();
  }

  protected Class<? extends ActionRequestDto> bundleExecutorMapKey() {
    return this.bundleExecutorMapKey;
  }

  protected void checkAccessResultCode(final AccessResultCode accessResultCode)
      throws ProtocolAdapterException {
    if (AccessResultCode.SUCCESS != accessResultCode) {
      throw new ProtocolAdapterException("AccessResultCode: " + accessResultCode);
    }
  }

  protected void checkMethodResultCode(final MethodResultCode methodResultCode)
      throws ProtocolAdapterException {
    if (MethodResultCode.SUCCESS != methodResultCode) {
      throw new ProtocolAdapterException("MethodResultCode: " + methodResultCode);
    }
  }

  /**
   * Retrieves connection, gets result data and validates it before returning.
   *
   * @param conn holds connection
   * @param getParameter for attribute to retrieve result data from
   * @return dataObject
   */
  public DataObject getValidatedResultData(
      final DlmsConnectionManager conn, final AttributeAddress getParameter)
      throws ProtocolAdapterException {
    final GetResult getResult;
    try {
      getResult = conn.getConnection().get(getParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }

    if (getResult == null) {
      throw new ProtocolAdapterException(
          "No GetResult received while retrieving M-Bus encryption key status.");
    }

    final DataObject dataObject = getResult.getResultData();
    if (!dataObject.isNumber()) {
      throw new ProtocolAdapterException("Received unexpected result data.");
    }
    return dataObject;
  }
}
