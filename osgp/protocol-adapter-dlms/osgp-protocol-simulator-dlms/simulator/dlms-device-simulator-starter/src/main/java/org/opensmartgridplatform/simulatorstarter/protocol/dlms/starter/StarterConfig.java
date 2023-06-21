// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulatorstarter.protocol.dlms.starter;

import java.io.IOException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan()
public class StarterConfig {
  public Starter starter(final ApplicationArguments applicationArguments) throws IOException {
    return new Starter(applicationArguments);
  }
}
