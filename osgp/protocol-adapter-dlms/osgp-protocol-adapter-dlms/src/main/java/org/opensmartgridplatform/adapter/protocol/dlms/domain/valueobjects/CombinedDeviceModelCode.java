// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * Container for combined DeviceModelCodes that contains a gateway device model code and multiple
 * channel based device model codes in the order of the channels 1-4. The format is :
 * GatewayDeviceModelCode,Channel1DeviceModelCode,Channel2DeviceModelCode,Channel3DeviceModelCode,Channel4DeviceModelCode
 */
public class CombinedDeviceModelCode {

  public static final String SEPERATOR = ",";

  private final String gatewayDeviceModelCode;
  private final Map<Integer, String> channelBasedDeviceModelCodes;

  private CombinedDeviceModelCode() {
    this.gatewayDeviceModelCode = "";
    this.channelBasedDeviceModelCodes = new HashMap<>();
  }

  private CombinedDeviceModelCode(
      final String gatewayDeviceModelCode,
      final Map<Integer, String> channelBasedDeviceModelCodes) {
    this.gatewayDeviceModelCode = gatewayDeviceModelCode;
    this.channelBasedDeviceModelCodes = channelBasedDeviceModelCodes;
  }

  public static CombinedDeviceModelCode parse(final String combinedDeviceModelCodes) {
    if (combinedDeviceModelCodes != null) {
      final String[] codes = combinedDeviceModelCodes.split(SEPERATOR);

      final String gatewayDeviceModelCode = codes[0];

      final Map<Integer, String> channelBasedDeviceModelCodes = new HashMap<>();
      for (int i = 1; i < codes.length; i++) {
        channelBasedDeviceModelCodes.put(i, codes[i]);
      }

      return new CombinedDeviceModelCode(gatewayDeviceModelCode, channelBasedDeviceModelCodes);
    } else {
      return new CombinedDeviceModelCode();
    }
  }

  public String getCodeFromChannel(final int i) {
    return this.channelBasedDeviceModelCodes.get(i);
  }

  @Override
  public String toString() {
    final List<String> codes = new ArrayList<>();
    codes.add(
        StringUtils.isNotBlank(this.gatewayDeviceModelCode) ? this.gatewayDeviceModelCode : "");
    for (int i = 1; i < 5; i++) {
      final String code = this.channelBasedDeviceModelCodes.get(i);
      codes.add(StringUtils.isNotBlank(code) ? code : "");
    }
    return String.join(SEPERATOR, codes);
  }

  public String getGatewayDeviceModelCode() {
    return this.gatewayDeviceModelCode;
  }

  // Builder Class
  public static class CombinedDeviceModelCodeBuilder {

    private String gatewayDeviceModelCode;
    private Map<Integer, String> channelBasedDeviceModelCodes;

    public CombinedDeviceModelCodeBuilder gatewayDeviceModelCode(
        final String gatewayDeviceModelCode) {
      this.gatewayDeviceModelCode = gatewayDeviceModelCode;
      return this;
    }

    public CombinedDeviceModelCodeBuilder channelBasedDeviceModelCode(
        final int channel, final String code) {
      if (this.channelBasedDeviceModelCodes == null) {
        this.channelBasedDeviceModelCodes = new HashMap<>();
      }
      this.channelBasedDeviceModelCodes.put(channel, code);
      return this;
    }

    public CombinedDeviceModelCode build() {
      return new CombinedDeviceModelCode(
          this.gatewayDeviceModelCode, this.channelBasedDeviceModelCodes);
    }
  }
}
