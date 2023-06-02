//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.config;

import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Base class for the application configuration. */
@Configuration
@PropertySource("classpath:cucumber-tests-platform-smartmetering.properties")
@PropertySource(
    value = "file:/etc/osp/test/global-cucumber.properties",
    ignoreResourceNotFound = true)
@PropertySource(
    value = "file:/etc/osp/test/cucumber-tests-platform-smartmetering.properties",
    ignoreResourceNotFound = true)
public class ApplicationConfiguration extends AbstractConfig {

  @Value("${web.service.template.default.uri.smartmetering.adhoc}")
  public String webserviceTemplateDefaultUriSmartMeteringAdHoc;

  @Value("${jaxb2.marshaller.context.path.smartmetering.adhoc}")
  public String contextPathSmartMeteringAdHoc;

  @Value("${web.service.template.default.uri.smartmetering.bundle}")
  public String webserviceTemplateDefaultUriSmartMeteringBundle;

  @Value("${jaxb2.marshaller.context.path.smartmetering.bundle}")
  public String contextPathSmartMeteringBundle;

  @Value("${web.service.template.default.uri.smartmetering.common}")
  public String webserviceTemplateDefaultUriSmartMeteringCommon;

  @Value("${jaxb2.marshaller.context.path.smartmetering.common}")
  public String contextPathSmartMeteringCommon;

  @Value("${web.service.template.default.uri.smartmetering.configuration}")
  public String webserviceTemplateDefaultUriSmartMeteringConfiguration;

  @Value("${jaxb2.marshaller.context.path.smartmetering.configuration}")
  public String contextPathSmartMeteringConfiguration;

  @Value("${web.service.template.default.uri.smartmetering.installation}")
  public String webserviceTemplateDefaultUriSmartMeteringInstallation;

  @Value("${jaxb2.marshaller.context.path.smartmetering.installation}")
  public String contextPathSmartMeteringInstallation;

  @Value("${web.service.template.default.uri.smartmetering.management}")
  public String webserviceTemplateDefaultUriSmartMeteringManagement;

  @Value("${jaxb2.marshaller.context.path.smartmetering.management}")
  public String contextPathSmartMeteringManagement;

  @Value("${web.service.template.default.uri.smartmetering.monitoring}")
  public String webserviceTemplateDefaultUriSmartMeteringMonitoring;

  @Value("${jaxb2.marshaller.context.path.smartmetering.monitoring}")
  public String contextPathSmartMeteringMonitoring;
}
