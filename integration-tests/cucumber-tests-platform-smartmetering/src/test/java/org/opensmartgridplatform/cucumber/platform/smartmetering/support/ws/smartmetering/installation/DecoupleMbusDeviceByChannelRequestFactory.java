//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class DecoupleMbusDeviceByChannelRequestFactory {

  private DecoupleMbusDeviceByChannelRequestFactory() {
    // Private constructor for utility class.
  }

  public static DecoupleMbusDeviceByChannelRequest fromGatewayAndChannel(
      final String gatewayDeviceIdentification, final String channel) {
    final DecoupleMbusDeviceByChannelRequest request = new DecoupleMbusDeviceByChannelRequest();
    request.setDeviceIdentification(gatewayDeviceIdentification);
    final DecoupleMbusDeviceByChannelRequestData requestData =
        new DecoupleMbusDeviceByChannelRequestData();
    requestData.setChannel(Short.parseShort(channel));
    request.setDecoupleMbusDeviceByChannelRequestData(requestData);
    return request;
  }

  public static DecoupleMbusDeviceByChannelRequest fromSettings(
      final Map<String, String> settings) {
    final DecoupleMbusDeviceByChannelRequest request = new DecoupleMbusDeviceByChannelRequest();
    final DecoupleMbusDeviceByChannelRequestData requestData =
        new DecoupleMbusDeviceByChannelRequestData();
    requestData.setChannel(Short.valueOf(settings.get(PlatformKeys.KEY_CHANNEL)));
    request.setDecoupleMbusDeviceByChannelRequestData(requestData);
    request.setDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    return request;
  }

  public static DecoupleMbusDeviceByChannelAsyncRequest fromScenarioContext() {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
    final DecoupleMbusDeviceByChannelAsyncRequest asyncRequest =
        new DecoupleMbusDeviceByChannelAsyncRequest();
    asyncRequest.setCorrelationUid(correlationUid);
    asyncRequest.setDeviceIdentification(deviceIdentification);
    return asyncRequest;
  }
}
