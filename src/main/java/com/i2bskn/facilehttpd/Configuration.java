package com.i2bskn.facilehttpd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Configuration {
  private static final Pattern absolute = Pattern.compile("^/");

  private String documentRoot;
  private String index;
  private String port;

  public Configuration() {
    setDefault();
  }

  public Configuration(String path) {
    setDefault();
    include(path);
  }

  public String getDocumentRoot() {
    return convertFullPath(documentRoot);
  }

  public String getIndex() {
    return index;
  }

  public int getPort() {
    return Integer.parseInt(port);
  }

  public void setDefault() {
    documentRoot = "./html";
    index = "index.html";
    port = "8080";
  }

  public void include(String path) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new File(path));
      Element root = doc.getDocumentElement();
      NodeList serversList = root.getElementsByTagName("servers");
      int serversListSize = serversList.getLength();
      for (int i = 0; i < serversListSize; i++) {
        Element servers = (Element)serversList.item(i);
        NodeList serverList = servers.getElementsByTagName("server");
        int serverListSize = serverList.getLength();
        for (int j = 0; j < serverListSize; j++) {
          Node server = serverList.item(j);
          setServerParams(server);
        }
      }
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      setDefault();
    }
  }

  private String getValue(Node node) {
    return node.getFirstChild().getNodeValue().trim();
  }

  private void setServerParams(Node server) {
    NodeList params = server.getChildNodes();
    int paramsSize = params.getLength();
    for (int i = 0; i < paramsSize; i++) {
      Node param = params.item(i);
      String nodeName = param.getNodeName();
      if (nodeName.equals("documentRoot")) {
        documentRoot = getValue(param);
      } else if (nodeName.equals("index")) {
        index = getValue(param);
      } else if (nodeName.equals("port")) {
        port = getValue(param);
      }
    }
  }

  private String convertFullPath(String path) {
    Matcher am = absolute.matcher(path);
    if (am.matches()) {
      return path;
    } else {
      Path fullpath = Paths.get(System.getProperty("user.dir") + "/" + path);
      return fullpath.normalize().toString();
    }
  }
}
