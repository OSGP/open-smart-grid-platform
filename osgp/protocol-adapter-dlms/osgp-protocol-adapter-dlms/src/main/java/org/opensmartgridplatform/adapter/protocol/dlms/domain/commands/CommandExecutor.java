/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

/**
 * Interface for executing a command on a smart meter over a client connection, taking input of type
 * <T>.
 *
 * @param <T> the type of object used as input for executing a command.
 * @param <R> the type of object returned as a result from executing a command.
 */
public interface CommandExecutor<T, R> {

  R execute(DlmsConnectionManager conn, DlmsDevice device, T object) throws OsgpException;

  /**
   * If a CommandExecutor gets called from an action that is part of a bundle, the result should
   * always be returned as an object that is assignable to ActionResponseDto from an input that is
   * an ActionRequestDto.
   *
   * @see #fromBundleRequestInput(ActionRequestDto)
   * @see #asBundleResponse(Object)
   */
  ActionResponseDto executeBundleAction(
      DlmsConnectionManager conn, DlmsDevice device, ActionRequestDto actionRequestDto)
      throws OsgpException;

  T fromBundleRequestInput(ActionRequestDto bundleInput) throws ProtocolAdapterException;

  ActionResponseDto asBundleResponse(R executionResult) throws ProtocolAdapterException;
}
