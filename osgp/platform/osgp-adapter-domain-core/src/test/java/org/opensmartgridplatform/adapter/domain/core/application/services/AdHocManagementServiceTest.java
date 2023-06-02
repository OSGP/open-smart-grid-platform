//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AdHocManagementServiceTest {
  @Mock private DeviceDomainService deviceDomainService;
  @Mock private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;
  @Mock private Device device;
  @Mock private OrganisationDomainService organisationDomainService;
  @InjectMocks private AdHocManagementService adHocManagementService;

  private static final int PRIORITY = 1;
  private static final String MESSAGE_TYPE = "testType";
  private static final String CORRELATION_UUID = "correlationUid";
  private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
  private static final String ORGANISATION_IDENTIFICATION = "orgIdentification";
  private static final String IP_ADDRESS = "127.0.0.1";

  @BeforeEach
  public void setup() throws Exception {
    when(this.device.getIpAddress()).thenReturn("127.0.0.1");
    when(this.deviceDomainService.searchActiveDevice(any(String.class), any(ComponentType.class)))
        .thenReturn(this.device);
  }

  @Test
  public void testSetReboot() throws Exception {
    this.givenOnlyTheIdentifiedOrganisationExists(ORGANISATION_IDENTIFICATION);

    final ArgumentCaptor<RequestMessage> messageCaptor =
        ArgumentCaptor.forClass(RequestMessage.class);
    this.adHocManagementService.setReboot(
        ORGANISATION_IDENTIFICATION,
        DEVICE_IDENTIFICATION,
        CORRELATION_UUID,
        MESSAGE_TYPE,
        PRIORITY);
    verify(this.osgpCoreRequestMessageSender)
        .send(messageCaptor.capture(), eq(MESSAGE_TYPE), eq(PRIORITY), eq(IP_ADDRESS));

    final RequestMessage message = messageCaptor.getValue();

    assertThat(message.getCorrelationUid()).isEqualTo(CORRELATION_UUID);
    assertThat(message.getOrganisationIdentification()).isEqualTo(ORGANISATION_IDENTIFICATION);
  }

  @Test
  public void testSetRebootThrowsError() throws Exception {
    this.givenOnlyTheIdentifiedOrganisationExists("wrongOrganisation");
    assertThatThrownBy(
            () ->
                this.adHocManagementService.setReboot(
                    ORGANISATION_IDENTIFICATION,
                    DEVICE_IDENTIFICATION,
                    CORRELATION_UUID,
                    MESSAGE_TYPE,
                    PRIORITY))
        .isInstanceOf(FunctionalException.class);
  }

  void givenOnlyTheIdentifiedOrganisationExists(final String organisationIdentification)
      throws Exception {
    when(this.organisationDomainService.searchOrganisation(anyString()))
        .thenAnswer(
            (Answer<Organisation>)
                invocation -> {
                  final String actualIdentification = invocation.getArgument(0);
                  if (organisationIdentification.equals(actualIdentification)) {
                    return Mockito.mock(Organisation.class);
                  }
                  throw new UnknownEntityException(Organisation.class, actualIdentification);
                });
  }
}
