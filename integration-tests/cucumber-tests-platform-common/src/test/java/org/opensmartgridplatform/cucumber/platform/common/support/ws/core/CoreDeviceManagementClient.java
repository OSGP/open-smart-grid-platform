// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.support.ws.core;

import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindEventsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindScheduledTasksRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindScheduledTasksResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceResponse;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class CoreDeviceManagementClient extends BaseClient {

  @Autowired private DefaultWebServiceTemplateFactory coreDeviceManagementWstf;

  public FindDevicesResponse findDevices(final FindDevicesRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindDevicesResponse) wst.marshalSendAndReceive(request);
  }

  public FindEventsResponse findEventsResponse(final FindEventsRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindEventsResponse) wst.marshalSendAndReceive(request);
  }

  public SetEventNotificationsResponse getSetEventNotificationsResponse(
      final SetEventNotificationsAsyncRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetEventNotificationsResponse) wst.marshalSendAndReceive(request);
  }

  public SetEventNotificationsAsyncResponse setEventNotifications(
      final SetEventNotificationsRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetEventNotificationsAsyncResponse) wst.marshalSendAndReceive(request);
  }

  public SetOwnerResponse setOwner(final SetOwnerRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetOwnerResponse) wst.marshalSendAndReceive(request);
  }

  public FindOrganisationResponse findOrganization(final FindOrganisationRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindOrganisationResponse) wst.marshalSendAndReceive(request);
  }

  public FindOrganisationResponse findOrganization(
      final String organizationIdentification, final FindOrganisationRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(organizationIdentification, this.getUserName());
    return (FindOrganisationResponse) wst.marshalSendAndReceive(request);
  }

  public FindAllOrganisationsResponse findAllOrganizations(
      final FindAllOrganisationsRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindAllOrganisationsResponse) wst.marshalSendAndReceive(request);
  }

  public FindAllOrganisationsResponse findAllOrganizations(
      final String organizationIdentification, final FindAllOrganisationsRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(organizationIdentification, this.getUserName());
    return (FindAllOrganisationsResponse) wst.marshalSendAndReceive(request);
  }

  public FindScheduledTasksResponse findScheduledTasks(final FindScheduledTasksRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (FindScheduledTasksResponse) wst.marshalSendAndReceive(request);
  }

  public SetDeviceLifecycleStatusAsyncResponse setDeviceLifecycleStatus(
      final SetDeviceLifecycleStatusRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetDeviceLifecycleStatusAsyncResponse) wst.marshalSendAndReceive(request);
  }

  public SetDeviceLifecycleStatusResponse getSetDeviceLifecycleStatusResponse(
      final SetDeviceLifecycleStatusAsyncRequest asyncRequest) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetDeviceLifecycleStatusResponse) wst.marshalSendAndReceive(asyncRequest);
  }

  public UpdateDeviceCdmaSettingsAsyncResponse updateDeviceCdmaSettings(
      final UpdateDeviceCdmaSettingsRequest request) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (UpdateDeviceCdmaSettingsAsyncResponse) wst.marshalSendAndReceive(request);
  }

  public UpdateDeviceCdmaSettingsResponse getUpdateDeviceCdmaSettingsResponse(
      final UpdateDeviceCdmaSettingsAsyncRequest asyncRequest) throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (UpdateDeviceCdmaSettingsResponse) wst.marshalSendAndReceive(asyncRequest);
  }

  public UpdateDeviceResponse updateDevice(final UpdateDeviceRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreDeviceManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (UpdateDeviceResponse) wst.marshalSendAndReceive(request);
  }
}
