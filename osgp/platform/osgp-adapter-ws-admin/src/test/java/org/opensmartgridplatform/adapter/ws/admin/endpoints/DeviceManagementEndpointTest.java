
package org.opensmartgridplatform.adapter.ws.admin.endpoints;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.ws.admin.application.mapping.DeviceManagementMapper;
import org.opensmartgridplatform.adapter.ws.admin.application.services.DeviceManagementService;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

@ExtendWith(MockitoExtension.class)
public class DeviceManagementEndpointTest {
    @Mock
    private DeviceManagementService deviceManagementService;
    @Mock
    private DeviceManagementMapper deviceManagementMapper;
    @Mock
    private CreateOrganisationRequest request;

    @InjectMocks
    private DeviceManagementEndpoint deviceManagementEndpoint;

    @BeforeEach
    public void setUp(){
        this.deviceManagementEndpoint = new DeviceManagementEndpoint(this.deviceManagementService, this.deviceManagementMapper);
    }

    @Test
    public void createOrganisationTest() throws OsgpException {
        final org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.Organisation organisation =
                new org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.Organisation();
        organisation.setOrganisationIdentification("orgIdentification");
        organisation.setName("orgName");
        organisation.setEnabled(true);

        when(this.request.getOrganisation()).thenReturn(organisation);
        when(this.deviceManagementMapper.map(any(), any()))
                .thenReturn(new org.opensmartgridplatform.domain.core.entities.Organisation());

        doThrow(new ConstraintViolationException("this is a contraintViolationException", new HashSet<>()))
                .when(this.deviceManagementService).addOrganisation(any(),
                any());

        try {
            verify(this.deviceManagementEndpoint.createOrganisation("orgIdentification",
                    this.request));
        } catch (final Exception e){
            assertThat(e).isInstanceOf(FunctionalException.class);
            System.out.println(e.getMessage());
        }
    }
}
