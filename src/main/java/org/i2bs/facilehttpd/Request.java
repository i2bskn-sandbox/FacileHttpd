package org.i2bs.facilehttpd;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Request {
  private InputStream input;

  public Request(Socket sock) throws IOException {
    input = sock.getInputStream();
  }

  public void close() throws IOException {
    input.close();
  }
}