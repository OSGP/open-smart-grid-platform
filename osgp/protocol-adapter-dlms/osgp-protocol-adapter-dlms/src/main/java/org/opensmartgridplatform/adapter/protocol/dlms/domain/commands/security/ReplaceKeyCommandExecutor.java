// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DeviceKeyProcessingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeysRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Some code may look odd, specifically in the execute() method. The reason is that the device may
 * (sometimes) return NOT_OK after a replacekeys request but was in fact successful! Actually the
 * situation is that (sometimes) the device returns NOT_OK but does replace the keys. So the key
 * that was sent to the device that received the status NOT_OK should be saved, so in case the
 * supposedly valid key (the key that was on the device before replace keys was executed) does not
 * work anymore the new (but supposedly NOT_OK) key can be tried. ! If that key works we know the
 * device gave the wrong response and this key should be made valid. See also DlmsDevice:
 * discardInvalidKeys, promoteInvalidKeys, get/hasNewSecurityKey.
 */
@Component
public class ReplaceKeyCommandExecutor
    extends AbstractReplaceKeyCommandExecutor<SetKeysRequestDto> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReplaceKeyCommandExecutor.class);

  @Autowired private DeviceKeyProcessingService deviceKeyProcessingService;

  @Autowired
  @Qualifier("decrypterForGxfSmartMetering")
  private RsaEncrypter decrypterForGxfSmartMetering;

  public ReplaceKeyCommandExecutor() {
    super(SetKeysRequestDto.class);
  }

  @Override
  public ActionResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetKeysRequestDto setKeysRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    this.checkActionRequestType(setKeysRequestDto);

    try {
      this.deviceKeyProcessingService.startProcessing(messageMetadata.getDeviceIdentification());
    } catch (final DeviceKeyProcessAlreadyRunningException e) {
      // This exception will be caught in the DeviceRequestMessageProcessor.
      // The request will NOT be sent back to Core to retry but put back on the queue
      LOGGER.info(
          "Key changing process already running on device :{}", device.getDeviceIdentification());
      throw e;
    }

    try {
      return this.replaceKeys(conn, device, setKeysRequestDto, messageMetadata);
    } finally {
      this.deviceKeyProcessingService.stopProcessing(messageMetadata.getDeviceIdentification());
    }
  }
}
