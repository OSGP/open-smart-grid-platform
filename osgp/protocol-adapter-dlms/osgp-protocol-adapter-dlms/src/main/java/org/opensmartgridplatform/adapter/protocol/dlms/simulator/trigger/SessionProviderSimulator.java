// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.simulator.trigger;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProvider;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProviderEnum;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.exceptions.SessionProviderException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * This class fakes being a SessionProvider. Instead, it sends a webrequest to start an instance of
 * a device simulator on-demand, returning the ip-address of the location where this device
 * simulator is started.
 *
 * <p>To work properly, an implementation of a device simulator needs to be present, and deployed.
 * This device simulator is not included in the source code of Protocol-Adapter-DLMS.
 *
 * <p>Besides the implementation of a device simulator, the url and ip-address of the location of
 * the web service should be provided in the config file.
 */
@Component
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true)
public class SessionProviderSimulator extends SessionProvider {

  @Value("${triggered.simulator.ipaddress}")
  private String ipAddress;

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private SimulatorTriggerClient simulatorTriggerClient;

  /**
   * Initialization function executed after dependency injection has finished. The SessionProvider
   * Singleton is added to the HashMap of SessionProviderMap.
   */
  @PostConstruct
  public void init() {
    this.sessionProviderMap.addProvider(SessionProviderEnum.SIMULATOR, this);
  }

  /**
   * This implementation depends on the iccId having the same value as the device identification (in
   * order to be able to look up some data with the device for calling the simulator starting web
   * service, like the port number and logicalId of a simulated device).
   *
   * @throws SessionProviderException when no dlmsDevice can be found with a deviceId equal to the
   *     given iccId, or the simulator was not successfully started.
   */
  @Override
  public String getIpAddress(final String iccId) throws SessionProviderException {

    DlmsDevice dlmsDevice;
    try {
      dlmsDevice = this.domainHelperService.findDlmsDevice(iccId);
      this.simulatorTriggerClient.sendTrigger(dlmsDevice);
    } catch (final FunctionalException e) {
      throw new SessionProviderException("Unable to find dlmsDevice. ", e);
    } catch (final SimulatorTriggerClientException e) {
      throw new SessionProviderException("Unable to successfully start a simulator. ", e);
    }

    return this.ipAddress;
  }
}
