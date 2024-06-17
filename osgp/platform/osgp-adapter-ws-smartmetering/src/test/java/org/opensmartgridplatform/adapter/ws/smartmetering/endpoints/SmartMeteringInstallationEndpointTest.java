// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMBusDeviceAdministrativeRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceAdministrativeAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceAdministrativeResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.Device;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeviceModel;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseUrlService;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.InstallationMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.RequestService;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceAdministrativeRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@ExtendWith(MockitoExtension.class)
class SmartMeteringInstallationEndpointTest {

  public static final String TEST_CORRELATION_ID_1234 = "test-correlation-id-1234";

  @Mock private RequestService requestService;

  @Mock private InstallationMapper installationMapper;

  @Mock protected ResponseUrlService responseUrlService;

  @Mock protected ResponseDataService responseDataService;

  @InjectMocks SmartMeteringInstallationEndpoint smartMeteringInstallationEndpoint;

  private static final String MBUS_DEVICE_IDENTIFICATION = "G1234";

  @Test
  /*
   * Verify that a decoupleMbusDeviceAdministrative will be handled and the DecoupleMbusDeviceAdministrativeRequestData is send with the correct Mbus Device ID.
   */
  void decoupleMbusDeviceAdministrative() throws OsgpException {
    final String messagePriority = "5";
    final String scheduleTime = "" + LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    final String responseUrl = "https://localhost/repsonse-url";
    final String bypassRetry = "true";

    final DecoupleMBusDeviceAdministrativeRequest request =
        mock(DecoupleMBusDeviceAdministrativeRequest.class);
    when(request.getMbusDeviceIdentification()).thenReturn(MBUS_DEVICE_IDENTIFICATION);

    final AsyncResponse asyncResponse = mock(AsyncResponse.class);
    when(asyncResponse.getCorrelationUid()).thenReturn(TEST_CORRELATION_ID_1234);

    final ArgumentCaptor<RequestMessageMetadata> requestMessageMetadataArgumentCaptor =
        ArgumentCaptor.forClass(RequestMessageMetadata.class);
    final ArgumentCaptor<DecoupleMbusDeviceAdministrativeRequestData>
        decoupleMbusDeviceAdministrativeRequestDataArgumentCaptor =
            ArgumentCaptor.forClass(DecoupleMbusDeviceAdministrativeRequestData.class);

    when(this.requestService.enqueueAndSendRequest(any(), any())).thenReturn(asyncResponse);

    this.smartMeteringInstallationEndpoint.decoupleMbusDeviceAdministrative(
        "test-org", request, messagePriority, scheduleTime, responseUrl, bypassRetry);

    verify(this.requestService)
        .enqueueAndSendRequest(
            requestMessageMetadataArgumentCaptor.capture(),
            decoupleMbusDeviceAdministrativeRequestDataArgumentCaptor.capture());

    verify(this.installationMapper).map(any(), any());

    Assertions.assertThat(
            decoupleMbusDeviceAdministrativeRequestDataArgumentCaptor
                .getValue()
                .getMbusDeviceIdentification())
        .isEqualTo(MBUS_DEVICE_IDENTIFICATION);
  }

  @Test
  void getDecoupleMbusDeviceAdministrativeResponse() throws OsgpException {
    final ResponseData responseData = mock(ResponseData.class);
    when(responseData.getResultType()).thenReturn(ResponseMessageResultType.OK);

    final DecoupleMbusDeviceAdministrativeAsyncRequest request =
        mock(DecoupleMbusDeviceAdministrativeAsyncRequest.class);
    when(request.getCorrelationUid()).thenReturn(TEST_CORRELATION_ID_1234);

    when(this.responseDataService.get(TEST_CORRELATION_ID_1234, ComponentType.WS_SMART_METERING))
        .thenReturn(responseData);

    final DecoupleMbusDeviceAdministrativeResponse response =
        this.smartMeteringInstallationEndpoint.getDecoupleMbusDeviceAdministrativeResponse(request);

    Assertions.assertThat(response.getResult()).isEqualTo(OsgpResultType.OK);
    Assertions.assertThat(response.getDescription()).isNull();
  }

  @Test
  void testAddDevice() throws OsgpException {
    final String organisationIdentification = "test-org";
    final AddDeviceRequest request = new AddDeviceRequest();
    final Device device = new Device();
    device.setDeviceIdentification("TestDevice");
    request.setDevice(device);
    request.setDeviceModel(new DeviceModel());
    request.setOverwrite(true);
    final String messagePriority = "5";
    final String scheduleTime = "123456789";
    final String responseUrl = "https://localhost/response-url";
    final String bypassRetry = "true";

    final SmartMeteringDevice smartMeteringDevice = new SmartMeteringDevice();
    smartMeteringDevice.setDeviceIdentification(device.getDeviceIdentification());
    when(this.installationMapper.map(device, SmartMeteringDevice.class))
        .thenReturn(smartMeteringDevice);

    final AsyncResponse asyncResponse = new AsyncResponse();
    asyncResponse.setCorrelationUid("TestCorrelationUid");
    asyncResponse.setDeviceIdentification(device.getDeviceIdentification());

    when(this.requestService.enqueueAndSendRequest(any(), any())).thenReturn(asyncResponse);

    this.smartMeteringInstallationEndpoint.addDevice(
        organisationIdentification,
        request,
        messagePriority,
        scheduleTime,
        responseUrl,
        bypassRetry);

    verify(this.requestService).enqueueAndSendRequest(any(), any(AddSmartMeterRequest.class));
  }

  @Test
  void testAddDeviceThrowsFunctionalException() throws OsgpException {
    final String organisationIdentification = "test-org";
    final AddDeviceRequest request = new AddDeviceRequest();
    final Device device = new Device();
    device.setDeviceIdentification("TestDevice");
    request.setDevice(device);
    request.setDeviceModel(new DeviceModel());
    request.setOverwrite(true);
    final String messagePriority = "5";
    final String scheduleTime = "123456789";
    final String responseUrl = "https://localhost/response-url";
    final String bypassRetry = "true";

    final SmartMeteringDevice smartMeteringDevice = new SmartMeteringDevice();
    smartMeteringDevice.setDeviceIdentification(device.getDeviceIdentification());
    when(this.installationMapper.map(device, SmartMeteringDevice.class))
        .thenReturn(smartMeteringDevice);

    when(this.requestService.enqueueAndSendRequest(any(), any()))
        .thenThrow(FunctionalException.class);

    assertThrows(
        FunctionalException.class,
        () -> {
          this.smartMeteringInstallationEndpoint.addDevice(
              organisationIdentification,
              request,
              messagePriority,
              scheduleTime,
              responseUrl,
              bypassRetry);
        });
  }
}
