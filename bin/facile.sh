#!/usr/bin/env bash

FACILE_HOME=$(cd "$(dirname $0)/../";pwd)
PID=$FACILE_HOME/log/facile.pid
LOG=$FACILE_HOME/log/facile.log
JAR=$FACILE_HOME/build/libs/FacileHttpd.jar
CONF=$FACILE_HOME/conf/Facile.xml

JAVA_HOME=${JAVA_HOME-/usr/local/java}
JAVA=$JAVA_HOME/bin/java
if [ ! -x $JAVA ]; then
  JAVA=$(which java)
  if [ -z $JAVA ]; then
    echo "java is not found."
    exit 100
  fi
fi

DAEMON=$(which daemon)
if [ -z $DAEMON ]; then
  echo "daemon is not found."
  exit 101
fi

CMD="$DAEMON -D $FACILE_HOME -F $PID -O $LOG -- $JAVA -jar $JAR $CONF &"
prog=FacileHttpd

sig() {
  test -s "$PID" && kill -$1 `cat $PID`
}

case $1 in
  start)
    echo -n "Starting $prog: "
    sig 0 && echo >&2 "Already running." && exit 0
    $CMD
    echo
    ;;
  stop)
    echo -n "Stopping $prog: "
    if sig 0; then
      sig TERM && echo
    else
      echo >&2 "Not running."
    fi
    ;;
  status)
    if sig 0; then
      echo "$prog is running."
    else
      echo "$prog is not running."
    fi
    ;;
  *)
    echo "Usage: `basename $0` {start|stop|status}"
    exit 1
    ;;
esac

exit 0