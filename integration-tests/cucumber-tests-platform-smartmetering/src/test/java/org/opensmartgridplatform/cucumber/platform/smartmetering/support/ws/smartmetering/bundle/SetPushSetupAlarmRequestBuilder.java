// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetPushSetupAlarmRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetPushSetupAlarmRequestBuilder {

  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_PORT = 9598;
  private static final String DEFAULT_CLASS_IDS = "1,40,1";
  private static final String DEFAULT_OBIS_CODES = "0.0.96.1.1.255,0.1.25.9.0.255,0.0.97.98.0.255";
  private static final String DEFAULT_ATTRIBUTE_IDS = "2,1,2";
  private static final String DEFAULT_DATA_INDEXES = "0,0,0";

  private String host;
  private BigInteger port;
  private List<PushObject> pushObjectList;

  public SetPushSetupAlarmRequestBuilder withDefaults() {
    return this.fromParameterMap(Collections.emptyMap());
  }

  public SetPushSetupAlarmRequestBuilder fromParameterMap(final Map<String, String> parameters) {
    this.host = this.getHost(parameters);
    this.port = this.getPort(parameters);
    this.pushObjectList = this.getPushObjectList(parameters);
    return this;
  }

  public SetPushSetupAlarmRequest build() {
    final SetPushSetupAlarmRequest request = new SetPushSetupAlarmRequest();
    final PushSetupAlarm pushSetupAlarm = new PushSetupAlarm();
    pushSetupAlarm.setHost(this.host);
    pushSetupAlarm.setPort(this.port);
    pushSetupAlarm.getPushObjectList().addAll(this.pushObjectList);

    request.setPushSetupAlarm(pushSetupAlarm);
    return request;
  }

  private String getHost(final Map<String, String> parameters) {
    return getString(parameters, PlatformSmartmeteringKeys.HOSTNAME, DEFAULT_HOST);
  }

  private BigInteger getPort(final Map<String, String> parameters) {
    return BigInteger.valueOf(getInteger(parameters, PlatformSmartmeteringKeys.PORT, DEFAULT_PORT));
  }

  private List<PushObject> getPushObjectList(final Map<String, String> parameters) {
    final List<String> pushObjectClassIds =
        Arrays.asList(getString(parameters, "PushObjectClassIds", DEFAULT_CLASS_IDS).split(","));
    final List<String> pushObjectObisCodes =
        Arrays.asList(getString(parameters, "PushObjectObisCodes", DEFAULT_OBIS_CODES).split(","));
    final List<String> pushObjectAttributeIds =
        Arrays.asList(
            getString(parameters, "PushObjectAttributeIds", DEFAULT_ATTRIBUTE_IDS).split(","));
    final List<String> pushObjectDataIndexes =
        Arrays.asList(
            getString(parameters, "PushObjectDataIndexes", DEFAULT_DATA_INDEXES).split(","));

    return IntStream.range(0, pushObjectClassIds.size())
        .mapToObj(
            i -> {
              final PushObject pushObject = new PushObject();

              pushObject.setClassId(Integer.parseInt(pushObjectClassIds.get(i)));
              pushObject.setLogicalName(this.convertObisCode(pushObjectObisCodes.get(i)));
              pushObject.setAttributeIndex(Byte.parseByte(pushObjectAttributeIds.get(i)));
              pushObject.setDataIndex(Integer.parseInt(pushObjectDataIndexes.get(i)));

              return pushObject;
            })
        .collect(Collectors.toList());
  }

  private ObisCodeValues convertObisCode(final String obisCode) {
    final String[] obisCodeSplit = obisCode.split("\\.");

    final ObisCodeValues obisCodeValues = new ObisCodeValues();
    obisCodeValues.setA(Short.parseShort(obisCodeSplit[0]));
    obisCodeValues.setB(Short.parseShort(obisCodeSplit[1]));
    obisCodeValues.setC(Short.parseShort(obisCodeSplit[2]));
    obisCodeValues.setD(Short.parseShort(obisCodeSplit[3]));
    obisCodeValues.setE(Short.parseShort(obisCodeSplit[4]));
    obisCodeValues.setF(Short.parseShort(obisCodeSplit[5]));

    return obisCodeValues;
  }
}
