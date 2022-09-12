package org.opensmartgridplatform.adapter.protocol.jasper;

import java.net.URI;
import java.net.URISyntaxException;

public class Test {

  public static void main(final String[] args) throws URISyntaxException {
    // TODO Auto-generated method stub
    final URI uri = new URI("https://restapi2.jasper.com");
    System.out.println(uri.getAuthority());
    System.out.println(uri.getFragment());
    System.out.println(uri.getHost());
    System.out.println(uri.getPath());
    System.out.println(uri.getPort());
  }
}
