//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.common.support.ws.admin;

import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.CreateOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindMessageLogsRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindMessageLogsResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RevokeKeyResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetCommunicationNetworkInformationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetCommunicationNetworkInformationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateKeyResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.AddManufacturerRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.ChangeManufacturerRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FindAllManufacturersRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FindAllManufacturersResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.RemoveManufacturerRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class AdminDeviceManagementClient extends BaseClient {

  @Autowired private DefaultWebServiceTemplateFactory adminDeviceManagementWstf;

  public ActivateOrganisationResponse activateOrganization(
      final ActivateOrganisationRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (ActivateOrganisationResponse) wst.marshalSendAndReceive(request);
  }

  public ChangeOrganisationResponse changeOrganization(final ChangeOrganisationRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (ChangeOrganisationResponse) wst.marshalSendAndReceive(request);
  }

  public CreateOrganisationResponse createOrganization(final CreateOrganisationRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (CreateOrganisationResponse) wst.marshalSendAndReceive(request);
  }

  public FindAllOrganisationsResponse findAllOrganizations(
      final FindAllOrganisationsRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindAllOrganisationsResponse) wst.marshalSendAndReceive(request);
  }

  public FindDeviceAuthorisationsResponse findDeviceAuthorisations(
      final FindDeviceAuthorisationsRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindDeviceAuthorisationsResponse) wst.marshalSendAndReceive(request);
  }

  public FindDevicesWhichHaveNoOwnerResponse findDevicesWithoutOwner(
      final FindDevicesWhichHaveNoOwnerRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindDevicesWhichHaveNoOwnerResponse) wst.marshalSendAndReceive(request);
  }

  public FindMessageLogsResponse findMessageLogs(final FindMessageLogsRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindMessageLogsResponse) wst.marshalSendAndReceive(request);
  }

  public RevokeKeyResponse getRevokeKeyResponse(final RevokeKeyRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (RevokeKeyResponse) wst.marshalSendAndReceive(request);
  }

  public UpdateKeyResponse getUpdateKeyResponse(final UpdateKeyRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (UpdateKeyResponse) wst.marshalSendAndReceive(request);
  }

  public RemoveDeviceResponse removeDevice(final RemoveDeviceRequest request)
      throws WebServiceSecurityException {
    return this.removeDevice(request, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
  }

  public RemoveDeviceResponse removeDevice(
      final RemoveDeviceRequest request, final String organizationIdentification)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(organizationIdentification, this.getUserName());
    return (RemoveDeviceResponse) wst.marshalSendAndReceive(request);
  }

  public RemoveOrganisationResponse removeOrganization(final RemoveOrganisationRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (RemoveOrganisationResponse) wst.marshalSendAndReceive(request);
  }

  public UpdateDeviceAuthorisationsResponse updateDeviceAuthorisations(
      final UpdateDeviceAuthorisationsRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (UpdateDeviceAuthorisationsResponse) wst.marshalSendAndReceive(request);
  }

  public SetOwnerResponse setOwner(final SetOwnerRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetOwnerResponse) wst.marshalSendAndReceive(request);
  }

  public UpdateDeviceProtocolResponse updateDeviceProtocol(
      final UpdateDeviceProtocolRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (UpdateDeviceProtocolResponse) wst.marshalSendAndReceive(request);
  }

  public GetProtocolInfosResponse getProtocolInfos(final GetProtocolInfosRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (GetProtocolInfosResponse) wst.marshalSendAndReceive(request);
  }

  public FindAllManufacturersResponse findAllManufacturers(
      final FindAllManufacturersRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindAllManufacturersResponse) wst.marshalSendAndReceive(request);
  }

  public AddManufacturerRequest addManufacturer(final AddManufacturerRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (AddManufacturerRequest) wst.marshalSendAndReceive(request);
  }

  public RemoveManufacturerRequest removeManufacturer(final RemoveManufacturerRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (RemoveManufacturerRequest) wst.marshalSendAndReceive(request);
  }

  public ChangeManufacturerRequest changeManufacturer(final ChangeManufacturerRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (ChangeManufacturerRequest) wst.marshalSendAndReceive(request);
  }

  public SetCommunicationNetworkInformationResponse setCommunicationNetworkInformation(
      SetCommunicationNetworkInformationRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.adminDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetCommunicationNetworkInformationResponse) wst.marshalSendAndReceive(request);
  }
}
