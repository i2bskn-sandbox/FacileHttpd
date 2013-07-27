package org.i2bs.facilehttpd;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Configuration {
  private static final Pattern absolute = Pattern.compile("^/");
  String docroot;
  String index;
  int port;

  public Configuration(String filepath) throws IOException {
    Properties prop = new Properties();
    prop.loadFromXML(new FileInputStream(filepath));
    docroot = getDocumentRoot(prop);
    index = prop.getProperty("Index", "index.html");
    port = Integer.parseInt(prop.getProperty("Port", "8080"));
  }

  private String getDocumentRoot(Properties p) {
    String dr = p.getProperty("DocumentRoot", "./html");
    Matcher am = absolute.matcher(dr);
    if (am.matches()) {
      return dr;
    } else {
      Path path = Paths.get(System.getProperty("user.dir") + "/" + dr);
      return path.normalize().toString();
    }
  }
}