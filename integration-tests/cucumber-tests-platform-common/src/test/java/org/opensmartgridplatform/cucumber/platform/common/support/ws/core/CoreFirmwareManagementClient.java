//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.common.support.ws.core;

import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.AddDeviceModelRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.AddFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.AddFirmwareResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.AddOrChangeFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.AddOrChangeFirmwareResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.ChangeDeviceModelRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.ChangeDeviceModelResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FindAllDeviceModelsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FindAllDeviceModelsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FindFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.RemoveDeviceModelRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.RemoveDeviceModelResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.RemoveFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.RemoveFirmwareResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareResponse;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class CoreFirmwareManagementClient extends BaseClient {

  @Autowired private DefaultWebServiceTemplateFactory coreFirmwareManagementWstf;

  public GetFirmwareVersionAsyncResponse getFirmwareVersion(final GetFirmwareVersionRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (GetFirmwareVersionAsyncResponse) wst.marshalSendAndReceive(request);
  }

  public GetFirmwareVersionResponse getGetFirmwareVersion(
      final GetFirmwareVersionAsyncRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (GetFirmwareVersionResponse) wst.marshalSendAndReceive(request);
  }

  public UpdateFirmwareAsyncResponse updateFirmware(final UpdateFirmwareRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (UpdateFirmwareAsyncResponse) wst.marshalSendAndReceive(request);
  }

  public UpdateFirmwareResponse getUpdateFirmware(final UpdateFirmwareAsyncRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (UpdateFirmwareResponse) wst.marshalSendAndReceive(request);
  }

  public ChangeFirmwareResponse changeFirmware(final ChangeFirmwareRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (ChangeFirmwareResponse) wst.marshalSendAndReceive(request);
  }

  public RemoveFirmwareResponse removeFirmware(final RemoveFirmwareRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (RemoveFirmwareResponse) wst.marshalSendAndReceive(request);
  }

  public AddFirmwareResponse addFirmware(final AddFirmwareRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (AddFirmwareResponse) wst.marshalSendAndReceive(request);
  }

  public AddOrChangeFirmwareResponse addOrChangeFirmware(final AddOrChangeFirmwareRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (AddOrChangeFirmwareResponse) wst.marshalSendAndReceive(request);
  }

  public ChangeDeviceModelResponse changeDeviceModel(final ChangeDeviceModelRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (ChangeDeviceModelResponse) wst.marshalSendAndReceive(request);
  }

  public RemoveDeviceModelResponse removeDeviceModel(final RemoveDeviceModelRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (RemoveDeviceModelResponse) wst.marshalSendAndReceive(request);
  }

  public FindAllDeviceModelsResponse findAllDeviceModels(final FindAllDeviceModelsRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindAllDeviceModelsResponse) wst.marshalSendAndReceive(request);
  }

  public AddDeviceModelRequest addDeviceModel(final AddDeviceModelRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (AddDeviceModelRequest) wst.marshalSendAndReceive(request);
  }

  public FindFirmwareRequest findFirmware(final FindFirmwareRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreFirmwareManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindFirmwareRequest) wst.marshalSendAndReceive(request);
  }
}
