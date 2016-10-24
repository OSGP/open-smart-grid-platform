/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleMessageRequest;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@RunWith(MockitoJUnitRunner.class)
public class BundleServiceTest {

    private static final String PREFIX = "prefix";
    private static final String NAME = "name";
    private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
    private static final String ORGANISATION_IDENTIFICATION = "organisationIdentification";
    private static final PlatformFunctionGroup FUNCTION_GROEP = PlatformFunctionGroup.USER;

    @InjectMocks
    BundleService bundleService;

    @Mock
    private DomainHelperService domainHelperService;

    @Mock
    private CorrelationIdProviderService correlationIdProviderService;

    @Mock
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    private Organisation organisation;
    private Device device;
    private List<ActionRequest> actionRequestMockList;

    @Before
    public void prepareTest() throws FunctionalException {
        this.organisation = new Organisation(ORGANISATION_IDENTIFICATION, NAME, PREFIX, FUNCTION_GROEP);
        this.device = new Device(DEVICE_IDENTIFICATION);
        this.actionRequestMockList = this.createActionRequestMockList();
        when(this.domainHelperService.findOrganisation(ORGANISATION_IDENTIFICATION)).thenReturn(this.organisation);
        when(this.domainHelperService.findActiveDevice(DEVICE_IDENTIFICATION)).thenReturn(this.device);

    }

    /**
     * tests that a {@link SmartMeteringRequestMessage} is send containing all
     * the {@link ActionRequest}s put in.
     *
     * @throws FunctionalException
     *             should not be thrown in this test
     */
    @Test
    public void testAllOperationsAreAllowed() throws FunctionalException {
        // Run the test
        this.bundleService.enqueueBundleRequest(ORGANISATION_IDENTIFICATION, DEVICE_IDENTIFICATION,
                this.actionRequestMockList, 1);

        // Verify the test
        final ArgumentCaptor<SmartMeteringRequestMessage> message = ArgumentCaptor
                .forClass(SmartMeteringRequestMessage.class);

        verify(this.smartMeteringRequestMessageSender).send(message.capture());

        assertEquals(message.getValue().getOrganisationIdentification(), ORGANISATION_IDENTIFICATION);
        assertEquals(message.getValue().getDeviceIdentification(), DEVICE_IDENTIFICATION);

        final BundleMessageRequest requestMessage = (BundleMessageRequest) message.getValue().getRequest();
        final List<ActionRequest> actionList = requestMessage.getBundleList();
        assertEquals(actionList.size(), this.actionRequestMockList.size());

        for (int i = 0; i < actionList.size(); i++) {
            assertEquals(this.actionRequestMockList.get(i), actionList.get(i));
        }
    }

    /**
     * tests that a {@link FunctionalException} is thrown when the caller is not
     * allowed to execute DeviceFunction.REQUEST_PERIODIC_METER_DATA
     * {@link ActionRequest} in the bundle
     *
     * @throws FunctionalException
     *             should not be thrown in this test
     */
    @Test
    public void testExceptionWhenOperationNotAllowed() throws FunctionalException {

        // Prepare test
        final FunctionalException fe = new FunctionalException(FunctionalExceptionType.UNAUTHORIZED,
                ComponentType.WS_SMART_METERING);
        doThrow(fe).when(this.domainHelperService).isAllowed(this.organisation, this.device,
                DeviceFunction.REQUEST_PERIODIC_METER_DATA);

        // Run the test
        try {
            this.bundleService.enqueueBundleRequest(ORGANISATION_IDENTIFICATION, DEVICE_IDENTIFICATION,
                    this.actionRequestMockList, 1);
            fail();
        } catch (final FunctionalException e) {
            // Verify the test
            assertEquals(fe, e);
        }
    }

    /**
     * tests that a {@link FunctionalException} is thrown when the caller is not
     * allowed to execute a bundle (DeviceFunction.HANDLE_BUNDLED_ACTIONS)
     *
     * @throws FunctionalException
     *             should not be thrown in this test
     */
    @Test
    public void testExceptionWhenBundleIsNotAllowed() throws FunctionalException {

        final FunctionalException fe = new FunctionalException(FunctionalExceptionType.UNAUTHORIZED,
                ComponentType.WS_SMART_METERING);

        doThrow(fe).when(this.domainHelperService).isAllowed(this.organisation, this.device,
                DeviceFunction.HANDLE_BUNDLED_ACTIONS);

        // Run the test
        try {
            this.bundleService.enqueueBundleRequest(ORGANISATION_IDENTIFICATION, DEVICE_IDENTIFICATION,
                    this.actionRequestMockList, 1);
        } catch (final FunctionalException e) {
            // Verify the test
            assertEquals(fe, e);
        }

    }

    private List<ActionRequest> createActionRequestMockList() {

        final ActionRequest a = mock(ActionRequest.class);
        final ActionRequest b = mock(ActionRequest.class);
        final ActionRequest c = mock(ActionRequest.class);
        final ActionRequest d = mock(ActionRequest.class);
        final ActionRequest e = mock(ActionRequest.class);
        final ActionRequest f = mock(ActionRequest.class);
        when(a.getDeviceFunction()).thenReturn(DeviceFunction.REQUEST_PERIODIC_METER_DATA);
        when(b.getDeviceFunction()).thenReturn(DeviceFunction.REQUEST_ACTUAL_METER_DATA);
        when(c.getDeviceFunction()).thenReturn(DeviceFunction.READ_ALARM_REGISTER);
        when(d.getDeviceFunction()).thenReturn(DeviceFunction.GET_FIRMWARE_VERSION);
        when(e.getDeviceFunction()).thenReturn(DeviceFunction.GET_SPECIFIC_ATTRIBUTE_VALUE);
        when(f.getDeviceFunction()).thenReturn(DeviceFunction.COUPLE_MBUS_DEVICE);

        return Arrays.asList(a, b, c, d, e, f);
    }
}
