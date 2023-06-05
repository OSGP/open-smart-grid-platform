// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

/**
 * Interface for executing a command on a smart meter over a client connection, taking input of type
 * <T>.
 *
 * @param <T> the type of object used as input for executing a command.
 * @param <R> the type of object returned as a result from executing a command.
 */
public interface CommandExecutor<T, R> {

  R execute(
      DlmsConnectionManager conn, DlmsDevice device, T object, MessageMetadata messageMetadata)
      throws OsgpException;

  /**
   * If a CommandExecutor gets called from an action that is part of a bundle, the result should
   * always be returned as an object that is assignable to ActionResponseDto from an input that is
   * an ActionRequestDto.
   *
   * @see #fromBundleRequestInput(ActionRequestDto)
   * @see #asBundleResponse(Object)
   */
  ActionResponseDto executeBundleAction(
      DlmsConnectionManager conn,
      DlmsDevice device,
      ActionRequestDto actionRequestDto,
      MessageMetadata messageMetadata)
      throws OsgpException;

  T fromBundleRequestInput(ActionRequestDto bundleInput) throws ProtocolAdapterException;

  ActionResponseDto asBundleResponse(R executionResult) throws ProtocolAdapterException;
}
