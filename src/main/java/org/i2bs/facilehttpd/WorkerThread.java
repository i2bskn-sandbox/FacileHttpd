package org.i2bs.facilehttpd;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.Socket;
import java.net.URLConnection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
      response(req);
      req.close();
      sock.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void response(Request req) throws IOException {
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
        header(req.version, 200, "OK", mimetype, (int)file.length());
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        OutputStream out = sock.getOutputStream();
        for (int c = in.read(); c >= 0; c = in.read()) {
          out.write(c);
        }
        in.close();
        out.close();
        tmp_out(req, 200);
      } else {
        String msg = "Not Found";
        header(req.version, 404, msg, "text/plain", msg.length());
        sendString(msg);
        tmp_out(req, 404);
      }
    } else {
      String msg = "Forbidden";
      header(req.version, 403, msg, "text/plain", msg.length());
      sendString(msg);
      tmp_out(req, 403);
    }
  }

  private void sendString(String content) throws IOException {
    PrintWriter output = new PrintWriter(sock.getOutputStream());
    output.println(content);
    output.flush();
    output.close();
  }

  private void header(String ver, int code, String msg, String type, int len) throws IOException {
    PrintWriter output = new PrintWriter(sock.getOutputStream());
    output.print("HTTP/");
    output.print(ver);
    output.print(" ");
    output.print(code);
    output.print(" ");
    output.println(msg);
    output.print("Content-Type: ");
    output.println(type);
    output.print("Content-Length: ");
    output.println(len);
    output.println("");
    output.flush();
  }

  private void tmp_out(Request req, int code) {
    Date now = new Date();
    System.out.print(now);
    System.out.print(" ");
    System.out.print(req.method);
    System.out.print(" ");
    System.out.print(req.path);
    System.out.print(" ");
    System.out.println(code);
  }
}
