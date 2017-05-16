/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.support.ws.admin;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindMessageLogsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindMessageLogsResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddManufacturerRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeManufacturerRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindAllManufacturersRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindAllManufacturersResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveManufacturerRequest;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.support.ws.BaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class AdminDeviceManagementClient extends BaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory adminDeviceManagementWstf;

    public ActivateDeviceResponse activateDevice(final ActivateDeviceRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (ActivateDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public ActivateOrganisationResponse activateOrganization(final ActivateOrganisationRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (ActivateOrganisationResponse) wst.marshalSendAndReceive(request);
    }

    public ChangeOrganisationResponse changeOrganization(final ChangeOrganisationRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (ChangeOrganisationResponse) wst.marshalSendAndReceive(request);
    }

    public CreateOrganisationResponse createOrganization(final CreateOrganisationRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (CreateOrganisationResponse) wst.marshalSendAndReceive(request);
    }

    public DeactivateDeviceResponse deactivateDevice(final DeactivateDeviceRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (DeactivateDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public FindAllOrganisationsResponse findAllOrganizations(final FindAllOrganisationsRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindAllOrganisationsResponse) wst.marshalSendAndReceive(request);
    }

    public FindDeviceAuthorisationsResponse findDeviceAuthorisations(final FindDeviceAuthorisationsRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindDeviceAuthorisationsResponse) wst.marshalSendAndReceive(request);
    }

    public FindDevicesWhichHaveNoOwnerResponse findDevicesWithoutOwner(final FindDevicesWhichHaveNoOwnerRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindDevicesWhichHaveNoOwnerResponse) wst.marshalSendAndReceive(request);
    }

    public FindMessageLogsResponse findMessageLogs(final FindMessageLogsRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindMessageLogsResponse) wst.marshalSendAndReceive(request);
    }

    public RevokeKeyResponse getRevokeKeyResponse(final RevokeKeyRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (RevokeKeyResponse) wst.marshalSendAndReceive(request);
    }

    public UpdateKeyResponse getUpdateKeyResponse(final UpdateKeyRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (UpdateKeyResponse) wst.marshalSendAndReceive(request);
    }

    public RemoveDeviceResponse removeDevice(final RemoveDeviceRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return this.removeDevice(request, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
    }

    public RemoveDeviceResponse removeDevice(final RemoveDeviceRequest request, final String organizationIdentification)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(organizationIdentification,
                this.getUserName());
        return (RemoveDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public RemoveOrganisationResponse removeOrganization(final RemoveOrganisationRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (RemoveOrganisationResponse) wst.marshalSendAndReceive(request);
    }

    public UpdateDeviceAuthorisationsResponse updateDeviceAuthorisations(
            final UpdateDeviceAuthorisationsRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (UpdateDeviceAuthorisationsResponse) wst.marshalSendAndReceive(request);
    }

    public SetOwnerResponse setOwner(final SetOwnerRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (SetOwnerResponse) wst.marshalSendAndReceive(request);
    }

    public UpdateDeviceProtocolResponse updateDeviceProtocol(final UpdateDeviceProtocolRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (UpdateDeviceProtocolResponse) wst.marshalSendAndReceive(request);
    }

    public GetProtocolInfosResponse getProtocolInfos(final GetProtocolInfosRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (GetProtocolInfosResponse) wst.marshalSendAndReceive(request);
    }

    public FindAllManufacturersResponse findAllManufacturers(final FindAllManufacturersRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindAllManufacturersResponse) wst.marshalSendAndReceive(request);
    }

    public AddManufacturerRequest addManufacturer(final AddManufacturerRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (AddManufacturerRequest) wst.marshalSendAndReceive(request);
    }

    public RemoveManufacturerRequest removeManufacturer(final RemoveManufacturerRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (RemoveManufacturerRequest) wst.marshalSendAndReceive(request);
    }

    public ChangeManufacturerRequest changeManufacturer(final ChangeManufacturerRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (ChangeManufacturerRequest) wst.marshalSendAndReceive(request);
    }
}
