/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetRandomisationSettingsRequestDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetRandomisationSettingsCommandExecutor
    extends AbstractCommandExecutor<SetRandomisationSettingsRequestDataDto, AccessResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetRandomisationSettingsCommandExecutor.class);

  private final DlmsObjectConfigService dlmsObjectConfigService;
  private final ProtocolServiceLookup protocolServiceLookup;

  @Autowired
  public SetRandomisationSettingsCommandExecutor(
      final DlmsObjectConfigService dlmsObjectConfigService,
      final ProtocolServiceLookup protocolServiceLookup) {
    super(SetRandomisationSettingsRequestDataDto.class);
    this.dlmsObjectConfigService = dlmsObjectConfigService;
    this.protocolServiceLookup = protocolServiceLookup;
  }

  @Override
  public SetRandomisationSettingsRequestDataDto fromBundleRequestInput(
      final ActionRequestDto bundleInput) throws ProtocolAdapterException {
    this.checkActionRequestType(bundleInput);
    return (SetRandomisationSettingsRequestDataDto) bundleInput;
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {
    this.checkAccessResultCode(executionResult);
    return new ActionResponseDto("Set Randomisation Settings was successful.");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetRandomisationSettingsRequestDataDto setRandomisationSettingsRequestDataDto)
      throws ProtocolAdapterException {

    LOGGER.info(
        "Executing SetRandomisationSettingsCommandExecutor. Data = {}",
        setRandomisationSettingsRequestDataDto);

    final boolean directAttach = setRandomisationSettingsRequestDataDto.getDirectAttach() == 1;
    final int randomisationStartWindow =
        setRandomisationSettingsRequestDataDto.getRandomisationStartWindow();
    final int multiplicationFactor =
        setRandomisationSettingsRequestDataDto.getMultiplicationFactor();
    final int numberOfRetries = setRandomisationSettingsRequestDataDto.getNumberOfRetries();

    if (directAttach) {
      LOGGER.info("Enabling directAttach on device {}.", device.getDeviceIdentification());
      this.writeDirectAttach(conn, device, true);
    } else {
      LOGGER.info(
          "Disabling directAttach and setting Randomisation Settings for device {}.",
          device.getDeviceIdentification());
      this.writeDirectAttach(conn, device, false);
      this.writeRandomisationSettings(
          conn, device, randomisationStartWindow, multiplicationFactor, numberOfRetries);
    }

    return AccessResultCode.SUCCESS;
  }

  private void writeRandomisationSettings(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final int randomisationStartWindow,
      final int multiplicationFactor,
      final int numberOfRetries)
      throws ProtocolAdapterException {
    final AttributeAddress randomisationSettingsAddress = this.getAttributeAddress(device);

    final DataObject randomisationStartWindowObject =
        DataObject.newUInteger32Data(randomisationStartWindow);
    final DataObject multiplicationFactorObject =
        DataObject.newUInteger16Data(multiplicationFactor);
    final DataObject numberOfRetriesObject = DataObject.newUInteger16Data(numberOfRetries);

    final DataObject randomisationSettingsObject =
        DataObject.newStructureData(
            randomisationStartWindowObject, multiplicationFactorObject, numberOfRetriesObject);

    final SetParameter setRandomisationSettings =
        new SetParameter(randomisationSettingsAddress, randomisationSettingsObject);
    this.writeAttribute(conn, setRandomisationSettings);
  }

  private void writeDirectAttach(
      final DlmsConnectionManager conn, final DlmsDevice device, final boolean directAttach)
      throws ProtocolAdapterException {

    final Protocol protocol = Protocol.forDevice(device);
    final GetConfigurationObjectService getConfigurationObjectService =
        this.protocolServiceLookup.lookupGetService(protocol);
    final ConfigurationObjectDto configurationOnDevice =
        getConfigurationObjectService.getConfigurationObject(conn);
    final SetConfigurationObjectService setService =
        this.protocolServiceLookup.lookupSetService(protocol);

    final ConfigurationObjectDto configurationToSet =
        this.createNewConfiguration(directAttach, configurationOnDevice);

    final AccessResultCode result =
        setService.setConfigurationObject(conn, configurationToSet, configurationOnDevice);

    this.checkResult(result, "directAttach");
  }

  private ConfigurationObjectDto createNewConfiguration(
      final boolean directAttach, final ConfigurationObjectDto configurationOnDevice) {
    final List<ConfigurationFlagDto> newConfiguration =
        new ArrayList<>(configurationOnDevice.getConfigurationFlags().getFlags());

    newConfiguration.removeIf(
        e -> e.getConfigurationFlagType() == ConfigurationFlagTypeDto.DIRECT_ATTACH_AT_POWER_ON);

    final ConfigurationFlagDto directAttachAtPowerOn =
        new ConfigurationFlagDto(ConfigurationFlagTypeDto.DIRECT_ATTACH_AT_POWER_ON, directAttach);

    newConfiguration.add(directAttachAtPowerOn);
    final ConfigurationFlagsDto configurationFlagsDto = new ConfigurationFlagsDto(newConfiguration);
    return new ConfigurationObjectDto(configurationFlagsDto);
  }

  private void writeAttribute(final DlmsConnectionManager conn, final SetParameter parameter)
      throws ProtocolAdapterException {
    try {
      final AccessResultCode result = conn.getConnection().set(parameter);
      this.checkResult(result, "setRandomisationSettings");
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  private void checkResult(final AccessResultCode result, final String attributeName)
      throws ProtocolAdapterException {
    if (!result.equals(AccessResultCode.SUCCESS)) {
      throw new ProtocolAdapterException(
          String.format(
              "Attribute '%s' of the Randomisation Settings was not set successfully. ResultCode: %s",
              attributeName, result.name()));
    }
  }

  private AttributeAddress getAttributeAddress(final DlmsDevice device)
      throws ProtocolAdapterException {
    final Optional<AttributeAddress> attributeAddress =
        this.dlmsObjectConfigService.findAttributeAddress(
            device, DlmsObjectType.RANDOMISATION_SETTINGS, null);
    return attributeAddress.orElseThrow(
        () ->
            new ProtocolAdapterException(
                "Could not find any configuration for DlmsObjectType.RANDOMISATION_SETTINGS"));
  }
}
