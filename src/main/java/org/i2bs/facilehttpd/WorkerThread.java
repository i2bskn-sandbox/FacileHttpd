package org.i2bs.facilehttpd;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.i2bs.facilehttpd.Request;

public class WorkerThread extends Thread {
  private Socket sock;

  public WorkerThread(Socket s) {
    sock = s;
  }

  public void run() {
    try {
      Request req = new Request(sock);
      hello();
      req.close();
      sock.close();
    } catch (Exception e) {
      System.out.println(e);
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
