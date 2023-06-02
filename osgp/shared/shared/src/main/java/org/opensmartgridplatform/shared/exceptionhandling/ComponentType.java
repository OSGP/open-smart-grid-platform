//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.exceptionhandling;

/** Enum having list of components used within OSGP */
public enum ComponentType {
  WS_ADMIN("Osgp Web Service Adapter Admin"),
  WS_CORE("Osgp Web Service Adapter Core"),
  WS_PUBLIC_LIGHTING("Osgp Web Service Adapter Public Lighting"),
  WS_TARIFF_SWITCHING("Osgp Web Service Adapter Tariff Switching"),
  WS_SMART_METERING("Osgp Web Service Adapter Smart Metering"),
  WS_MICROGRIDS("Osgp Web Service Adapter Microgrids"),
  WS_DISTRIBUTION_AUTOMATION("Osgp Web Service Adapter Distribution Automation"),
  KAFKA_DISTRIBUTION_AUTOMATION("Osgp Kafka Adapter Distribution Automation"),
  DOMAIN_ADMIN("Osgp Domain Adapter Admin"),
  DOMAIN_CORE("Osgp Domain Adapter Core"),
  DOMAIN_PUBLIC_LIGHTING("Osgp Domain Adapter Public Lighting"),
  DOMAIN_TARIFF_SWITCHING("Osgp Domain Adapter Tariff Switching"),
  DOMAIN_SMART_METERING("Osgp Domain Adapter Smart Metering"),
  DOMAIN_MICROGRIDS("Osgp Domain Adapter Microgrids"),
  DOMAIN_DISTRIBUTION_AUTOMATION("Osgp Domain Adapter Distribution Automation"),
  OSGP_CORE("Osgp Core"),
  PROTOCOL_OSLP("Osgp Protocol Adapter OSLP"),
  PROTOCOL_DLMS("Osgp Protocol Adapter DLMS"),
  PROTOCOL_IEC61850("Osgp Protocol Adapter IEC61850"),
  PROTOCOL_IEC60870("Osgp Protocol Adapter IEC60870"),
  PROTOCOL_MQTT("Osgp Protocol Adapter MQTT"),
  SECRET_MANAGEMENT("Secret Management"),
  SHARED("Shared"),

  UNKNOWN("Unknown");

  private final String componentName;

  private ComponentType(final String componentName) {
    this.componentName = componentName;
  }

  public String getComponentName() {
    return this.componentName;
  }
}
