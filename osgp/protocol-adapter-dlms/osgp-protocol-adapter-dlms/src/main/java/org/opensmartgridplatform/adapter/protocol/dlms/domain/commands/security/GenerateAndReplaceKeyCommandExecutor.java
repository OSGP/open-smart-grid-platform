// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_ENCRYPTION;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DeviceKeyProcessingService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GenerateAndReplaceKeysRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeysRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenerateAndReplaceKeyCommandExecutor
    extends AbstractReplaceKeyCommandExecutor<GenerateAndReplaceKeysRequestDataDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GenerateAndReplaceKeyCommandExecutor.class);

  @Autowired private SecretManagementService secretManagementService;

  @Autowired private DeviceKeyProcessingService deviceKeyProcessingService;

  public GenerateAndReplaceKeyCommandExecutor() {
    super(GenerateAndReplaceKeysRequestDataDto.class);
  }

  @Override
  public ActionResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GenerateAndReplaceKeysRequestDataDto actionRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {

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
      LOGGER.info("Generate new keys for device {}", device.getDeviceIdentification());
      final SetKeysRequestDto setKeysRequest =
          this.generateSetKeysRequest(messageMetadata, device.getDeviceIdentification());
      return this.replaceKeys(conn, device, setKeysRequest, messageMetadata);
    } finally {
      this.deviceKeyProcessingService.stopProcessing(messageMetadata.getDeviceIdentification());
    }
  }

  private SetKeysRequestDto generateSetKeysRequest(
      final MessageMetadata messageMetadata, final String deviceIdentification)
      throws FunctionalException {
    try {
      final List<SecurityKeyType> keyTypes =
          Arrays.asList(E_METER_AUTHENTICATION, E_METER_ENCRYPTION);
      final Map<SecurityKeyType, byte[]> generatedKeys =
          this.secretManagementService.generate128BitsKeysAndStoreAsNewKeys(
              messageMetadata, deviceIdentification, keyTypes);
      final SetKeysRequestDto setKeysRequest =
          new SetKeysRequestDto(
              generatedKeys.get(E_METER_AUTHENTICATION), generatedKeys.get(E_METER_ENCRYPTION));
      setKeysRequest.setGeneratedKeys(true);
      return setKeysRequest;
    } catch (final EncrypterException e) {
      throw new FunctionalException(
          FunctionalExceptionType.ENCRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS, e);
    }
  }
}
