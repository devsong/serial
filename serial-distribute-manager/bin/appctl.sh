#!/bin/bash

source /etc/profile
PROG_NAME=$0
ACTION=$1

usage() {
    echo "Usage: $PROG_NAME {start|stop|restart|status|tailf|backup}"
    exit 1;
}

if [ "$UID" -eq 0 ]; then
    echo "can't run as root, please use: sudo -u admin $0 $@"
    exit 1
fi

if [ $# -lt 1 ]; then
    usage
fi

# colors
red='\e[0;31m'
green='\e[0;32m'
yellow='\e[0;33m'
reset='\e[0m'

echoRed() { echo -e "${red}$1${reset}"; }
echoGreen() { echo -e "${green}$1${reset}"; }
echoYellow() { echo -e "${yellow}$1${reset}"; }

APP_HOME=$(cd $(dirname $0)/..; pwd)
app=$(echo "$APP_HOME"|awk -F '/' '{print $NF}')
cd $APP_HOME
mkdir -p logs

jar_home=.
main=com.gzs.learn.serial.SerialManagerServer
module=serial-distribute-manager-1.0.0-SNAPSHOT.jar
pidfile=logs/app.pid
logfile=logs/info.`date +%F`.log
JAVA_OPTS="-server -Xms1024m -Xmx1024m -XX:NewSize=256m -XX:MaxNewSize=512m -Xss256k -Ddubbo.application.logger=slf4j"

function check_pid() {
    if [ -f $pidfile ];then
        pid=`cat $pidfile`
        if [ -n $pid ]; then
            running=`ps -p $pid|grep -v "PID TTY" |wc -l`
            return $running
        fi
    fi
    return 0
}

function start() {
    check_pid
    running=$?
    if [ $running -gt 0 ];then
        echoGreen "$app now is running already, pid=`cat $pidfile`"
        return 1
    fi

 	nohup java $JAVA_OPTS -cp $jar_home/lib/*:$jar_home/conf:$jar_home/$module $main >> ${logfile} 2>&1 & pid=$!
  
    echoGreen "$app starting "
    for e in $(seq 10); do
        echo -n " $e"
        sleep 1
    done
    echo $pid > $pidfile
    check_pid
    running=$?
    if [ $running -gt 0 ];then
        echoGreen " ,pid=`cat $pidfile`"
        return 1
    else
	echoRed ",started fail!!!"
    fi
}

function stop() {
    pid=`cat $pidfile`
    kill -9 $pid
    echoRed "$app stoped..."
}

function restart() {
    stop
    sleep 1
    start
}

function tailf() {
        tail -f $APP_HOME/$logfile
}

function status() {
    check_pid
    running=$?
    if [ $running -gt 0 ];then
        echoGreen "$app now is running, pid=`cat $pidfile`"
    else
        echoYellow "$app is stoped"
    fi
}

function backup() {
    bakdir=/home/admin/backup/`date +%F`
    mkdir -p $bakdir
    cd $APP_HOME/..
    tar -zcf $app-`date +'%Y%m%d%H%M'`.tar.gz $app --exclude=$app/logs
    mv $app-`date +'%Y%m%d%H%M'`.tar.gz $bakdir
    echoGreen "$app deploy success, is $app-`date +'%Y%m%d%H%M'`.tar.gz"
}

function main {
   RETVAL=0
   case "$1" in
      start)
         start
         ;;
      stop)
         stop
         ;;
      restart)
         restart
         ;;
      tailf)
         tailf
         ;;
      status)
         status
         ;;
      backup)
         backup
         ;;
      *)
	 usage
         ;;
      esac
   exit $RETVAL
}

main $1