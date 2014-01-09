package com.i2bskn.facilehttpd;

import java.net.ServerSocket;
import java.net.Socket;

public class FacileHttpd {
  private ServerSocket ssock;
  private Configuration config;

  public FacileHttpd(Configuration config) {
    try {
      this.config = config;
      ssock = new ServerSocket(config.getPort());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void start() {
    try {
      while (true) {
        Socket sock = ssock.accept();
        WorkerThread worker = new WorkerThread(sock, config.getDocumentRoot(), config.getIndex());
        worker.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void close() {
    try {
      ssock.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Should specify the configuration file as an argument");
      System.exit(1);
    }

    try {
      Configuration configuration = new Configuration(args[0]);
      final FacileHttpd server = new FacileHttpd(configuration);
      Runtime.getRuntime().addShutdownHook(new Thread(){
        public void run() {
          server.close();
        }
      });
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
