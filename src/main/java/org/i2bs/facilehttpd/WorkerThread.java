package org.i2bs.facilehttpd;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.net.URLConnection;

import org.i2bs.facilehttpd.Request;

public class WorkerThread extends Thread {
  private static final Pattern addindex = Pattern.compile("/$");
  private Socket sock;
  String docroot;
  String indexfile;

  public WorkerThread(Socket s, String dr, String inf) {
    sock = s;
    docroot = dr;
    indexfile = inf;
  }

  public void run() {
    try {
      Request req = new Request(sock);
      sendfile(req);
      hello();
      req.close();
      sock.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void sendfile(Request req) throws IOException {
    Path path = null;
    Matcher im = addindex.matcher(req.path);
    if (im.matches()) {
      path = Paths.get(docroot + req.path + indexfile);
    } else {
      path = Paths.get(docroot + req.path);
    }

    String filepath = path.normalize().toString();
    if (filepath.startsWith(docroot)) {
      File file = new File(filepath);
      if (file.exists()) {
        String mimetype = URLConnection.guessContentTypeFromName(file.getName());
        System.out.println("exists!");
      } else {
        System.out.println("404 Not Found");
      }
    } else {
      System.out.println("403 Forbidden");
    }
  }

  private void hello() throws IOException {
    String content = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><title>Sample</title></head><body><h1>Hello World!</h1></body></html>";
    header(content.length());
    PrintWriter output = new PrintWriter(sock.getOutputStream());
    output.println(content);
    output.flush();
    output.close();
  }

  private void header(int len) throws IOException {
    PrintWriter output = new PrintWriter(sock.getOutputStream());
    output.println("HTTP/1.1 200 OK");
    output.println("Content-Type: text/html");
    output.print("Content-Length: ");
    output.println(len);
    output.println("");
    output.flush();
  }
}
