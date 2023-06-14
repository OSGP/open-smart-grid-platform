// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlagType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData;

public class SetConfigurationObjectRequestMappingTest {

  private static final String DEVICE_ID = "id1";
  private static final ConfigurationFlagType FLAGTYPE =
      ConfigurationFlagType.DISCOVER_ON_OPEN_COVER;
  private static final GprsOperationModeType GPRSTYPE = GprsOperationModeType.ALWAYS_ON;
  private static final boolean ISENABLED = true;
  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  /** Tests if mapping succeeds with a complete SetConfigurationRequestData object. */
  @Test
  public void testWithCompleteObject() {

    // build test data
    final ConfigurationObject configurationObject = new ConfigurationObject();
    final ConfigurationFlag configurationFlag = new ConfigurationFlag();
    configurationFlag.setConfigurationFlagType(FLAGTYPE);
    configurationFlag.setEnabled(ISENABLED);
    final ConfigurationFlags configurationFlags = new ConfigurationFlags();
    configurationFlags.getConfigurationFlag().add(configurationFlag);
    configurationObject.setConfigurationFlags(configurationFlags);
    configurationObject.setGprsOperationMode(GPRSTYPE);
    final SetConfigurationObjectRequestData setConfigurationObjectRequestData =
        new SetConfigurationObjectRequestData();
    setConfigurationObjectRequestData.setConfigurationObject(configurationObject);
    final SetConfigurationObjectRequest requestOriginal = new SetConfigurationObjectRequest();
    requestOriginal.setDeviceIdentification(DEVICE_ID);
    requestOriginal.setSetConfigurationObjectRequestData(setConfigurationObjectRequestData);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .SetConfigurationObjectRequest
        requestMapped =
            this.configurationMapper.map(
                requestOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .SetConfigurationObjectRequest.class);

    // check mapping
    assertThat(requestMapped).isNotNull();
    assertThat(requestMapped.getDeviceIdentification()).isEqualTo(DEVICE_ID);
    assertThat(requestMapped.getSetConfigurationObjectRequestData()).isNotNull();
    assertThat(requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject())
        .isNotNull();
    assertThat(
            requestMapped
                .getSetConfigurationObjectRequestData()
                .getConfigurationObject()
                .getGprsOperationMode())
        .isNotNull();
    assertThat(
            requestMapped
                .getSetConfigurationObjectRequestData()
                .getConfigurationObject()
                .getConfigurationFlags())
        .isNotNull();
    assertThat(
            requestMapped
                .getSetConfigurationObjectRequestData()
                .getConfigurationObject()
                .getConfigurationFlags()
                .getFlags())
        .isNotNull();
    assertThat(
            requestMapped
                .getSetConfigurationObjectRequestData()
                .getConfigurationObject()
                .getConfigurationFlags()
                .getFlags()
                .get(0))
        .isNotNull();
    assertThat(
            requestMapped
                .getSetConfigurationObjectRequestData()
                .getConfigurationObject()
                .getConfigurationFlags()
                .getFlags()
                .get(0)
                .getConfigurationFlagType())
        .isNotNull();
    assertThat(
            requestMapped
                .getSetConfigurationObjectRequestData()
                .getConfigurationObject()
                .getGprsOperationMode()
                .name())
        .isEqualTo(GPRSTYPE.name());
    assertThat(
            requestMapped
                .getSetConfigurationObjectRequestData()
                .getConfigurationObject()
                .getConfigurationFlags()
                .getFlags()
                .get(0)
                .getConfigurationFlagType()
                .name())
        .isEqualTo(FLAGTYPE.name());
  }

  /** Tests if mapping succeeds when ConfigurationObject is null. */
  @Test
  public void testWithNullConfigurationObject() {
    // build test data
    final ConfigurationObject configurationObject = null;
    final SetConfigurationObjectRequestData setConfigurationObjectRequestData =
        new SetConfigurationObjectRequestData();
    setConfigurationObjectRequestData.setConfigurationObject(configurationObject);
    final SetConfigurationObjectRequest requestOriginal = new SetConfigurationObjectRequest();
    requestOriginal.setDeviceIdentification(DEVICE_ID);
    requestOriginal.setSetConfigurationObjectRequestData(setConfigurationObjectRequestData);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .SetConfigurationObjectRequest
        requestMapped =
            this.configurationMapper.map(
                requestOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .SetConfigurationObjectRequest.class);

    // check mapping
    assertThat(requestMapped).isNotNull();
    assertThat(requestMapped.getDeviceIdentification()).isNotNull();
    assertThat(requestMapped.getDeviceIdentification()).isEqualTo(DEVICE_ID);
    assertThat(requestMapped.getSetConfigurationObjectRequestData()).isNotNull();
    assertThat(requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject())
        .isNull();
  }

  /** Tests if mapping succeeds when SetConfigurationObjectRequestData is null. */
  @Test
  public void testWithNullSetConfigurationObjectRequestData() {

    // build test data
    final SetConfigurationObjectRequestData setConfigurationObjectRequestData = null;
    final SetConfigurationObjectRequest requestOriginal = new SetConfigurationObjectRequest();
    requestOriginal.setDeviceIdentification(DEVICE_ID);
    requestOriginal.setSetConfigurationObjectRequestData(setConfigurationObjectRequestData);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .SetConfigurationObjectRequest
        requestMapped =
            this.configurationMapper.map(
                requestOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .SetConfigurationObjectRequest.class);

    // check mapping
    assertThat(requestMapped).isNotNull();
    assertThat(requestMapped.getDeviceIdentification()).isNotNull();
    assertThat(requestMapped.getDeviceIdentification()).isEqualTo(DEVICE_ID);
    assertThat(requestMapped.getSetConfigurationObjectRequestData()).isNull();
  }
}
