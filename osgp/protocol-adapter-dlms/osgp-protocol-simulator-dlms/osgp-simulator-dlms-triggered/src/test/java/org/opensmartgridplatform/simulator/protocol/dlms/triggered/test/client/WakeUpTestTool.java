/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.triggered.test.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can be used to test starting 10 device simulators via a webrequest, on 10 different
 * portnumbers (5000 - 5009).
 *
 * <p>Please make sure that the proper application is running and that the BASE_URL is configured to
 * send the request to the correct place, before attempting to run this test.
 */
public class WakeUpTestTool {

  private static final String BASE_URL =
      "http://localhost:8080/osgp-simulator-dlms-triggered/wakeup/trigger";
  private static final Logger LOGGER = LoggerFactory.getLogger(WakeUpTestTool.class);

  public static void main(final String[] args) {

    for (int i = 0; i < 10; i++) {
      final int port = 5000 + i;
      wakeUp(port);
      try {
        Thread.sleep(1000);
      } catch (final InterruptedException e) {
        LOGGER.warn("Thread was interrupted", e);
      }
    }
  }

  private static void wakeUp(final int port) {

    try {
      final HttpClient httpClient = HttpClientBuilder.create().build();
      final String url = BASE_URL + "?ipaddress=127.0.0.1&port=" + port;
      final HttpGet getRequest = new HttpGet(url);
      getRequest.addHeader("accept", "application/json");
      final HttpResponse response = httpClient.execute(getRequest);
      checkResponseCode(response);
      final BufferedReader br =
          new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
      String output;
      System.out.println("============Output:============");
      while ((output = br.readLine()) != null) {
        System.out.println(output);
      }

    } catch (final Exception e) {
      LOGGER.warn("Unable to wakeup device simulator", e);
    }
  }

  private static void checkResponseCode(final HttpResponse response) {
    if (response.getStatusLine().getStatusCode() == 500) {
      throw new RuntimeException("Failed to start a simulator : Port was already in use. ");
    } else if (response.getStatusLine().getStatusCode() != 200) {
      throw new RuntimeException(
          "Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
    }
  }
}
