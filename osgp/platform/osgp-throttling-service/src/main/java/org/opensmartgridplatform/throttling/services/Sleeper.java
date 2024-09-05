package org.opensmartgridplatform.throttling.services;

import org.springframework.stereotype.Component;

@Component
public class Sleeper {

  public void sleep(final long millis) throws InterruptedException {
    Thread.sleep(millis);
  }
}
