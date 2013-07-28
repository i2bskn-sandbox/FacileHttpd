Facile HTTPD
============

Sample http server on Linux/OSX.  
while creating...

Dependencies
------------

* Java 1.7
* `gradle` required for build.
* `daemon` required for start as a daemon. [daemon](http://libslack.org/daemon/)

Installation
------------

#### Gradle

    cd /usr/local/src
    wget -c http://services.gradle.org/distributions/gradle-1.6-bin.zip
    unzip gradle-1.6-bin.zip
    mv -i gradle-1.6 /usr/local/gradle
    export GRADLE_HOME=/usr/local/gradle
    export PATH=$PATH:$GRADLE_HOME/bin
    gradle -v

#### Daemon

    cd /usr/local/src
    wget -c http://libslack.org/daemon/download/daemon-0.6.4.tar.gz
    tar zxvf daemon-0.6.4.tar.gz
    cd daemon-0.6.4
    ./configure
    make && make install
    daemon -V

#### FacileHttpd

    cd /usr/local
    git clone git://github.com/i2bskn/FacileHttpd.git
    cd FacileHttpd
    gradle build

Configuration
-------------

Edit the `conf/Facile.xml`.

Usage
-----

Start/Stop/Status:

    FacileHttpd/bin/facile.sh {start|stop|status}
