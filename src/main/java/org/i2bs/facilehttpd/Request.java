package org.i2bs.facilehttpd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
// import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
  private static final Pattern border = Pattern.compile("^$");
  private static final Pattern request = Pattern.compile("^(.*) (.*) HTTP/(.*)$");
  private InputStream input;
  String method = null;
  String path = null;
  String version = null;

  public Request(Socket sock) throws IOException {
    input = sock.getInputStream();
    // ArrayList<String> headers = new ArrayList<String>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

    String inline = reader.readLine();
    while (reader.ready() && inline != null) {
      Matcher bm = border.matcher(inline);
      if (bm.matches()) break;

      if (method == null) {
        Matcher rm = request.matcher(inline);
        if (rm.matches()) {
          method = rm.group(1);
          path = rm.group(2);
          version = rm.group(3);
          break;
        }
      }

      // headers.add(inline);
      inline = reader.readLine();
    }
  }

  public void close() throws IOException {
    input.close();
  }
}